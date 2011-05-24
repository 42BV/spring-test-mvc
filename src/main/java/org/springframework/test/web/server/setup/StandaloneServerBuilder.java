/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.server.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.server.AbstractMvcServerBuilder;
import org.springframework.test.web.server.MockMvcServer;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

/**
 * Builds a {@link MockMvcServer} by instantiating the required Spring MVC components directly rather than detecting
 * them in a Spring ApplicationContext. This makes it possible to build more "lightweight" and very focused tests
 * involving one or just a few controllers.
 * 
 * <p>The resulting setup is geared at supporting controllers with @{@link RequestMapping} methods. View resolution 
 * can be configured by providing a list of {@link ViewResolver}s. When view resolution is left not configured, a
 * fixed, no-op {@link View} is used effectively ignoring rendering.
 * 
 */ 
public class StandaloneServerBuilder extends AbstractMvcServerBuilder {
	
	private final Object[] controllers;
	
	private List<HttpMessageConverter<?>> messageConverters;
	
	private Validator validator;
	
	private FormattingConversionService conversionService = new DefaultFormattingConversionService();
	
	private final List<MappedInterceptor> mappedInterceptors = new ArrayList<MappedInterceptor>();

	private List<? extends ViewResolver> viewResolvers;

	private GenericWebApplicationContext applicationContext;

	protected StandaloneServerBuilder(Object[] controllers) {
		Assert.isTrue(!ObjectUtils.isEmpty(controllers), "At least one controller is required");
		this.controllers = controllers;
	}

	public StandaloneServerBuilder setMessageConverters(HttpMessageConverter<?>...messageConverters) {
		this.messageConverters = Arrays.asList(messageConverters);
		return this;
	}

	public StandaloneServerBuilder setValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	public StandaloneServerBuilder setConversionService(FormattingConversionService conversionService) {
		this.conversionService = conversionService;
		return this;
	}
	
	public StandaloneServerBuilder addInterceptors(HandlerInterceptor... interceptors) {
		mapInterceptors(null, interceptors);
		return this;
	}

	public StandaloneServerBuilder mapInterceptors(String[] pathPatterns, HandlerInterceptor... interceptors) {
		for (HandlerInterceptor interceptor : interceptors) {
			mappedInterceptors.add(new MappedInterceptor(pathPatterns, interceptor));
		}
		return this;
	}
	
	/**
	 * Configures a single ViewResolver that always renders using the provided View implementation.
	 * Provides a simple way to render generated content (e.g. JSON, XML, Atom, etc.) For URL-based view types, 
	 * i.e. sub-classes of AbstractUrlBasedView, use {@link #setViewResolvers(ViewResolver...)} instead.
	 * 
	 * @param view the default View to return for any view name
	 */
	public StandaloneServerBuilder configureFixedViewResolver(View view) {
		viewResolvers = Collections.singletonList(new FixedViewResolver(view));
		return this;
	}

	/**
	 * Configures view resolution with the given {@link ViewResolver}s.
	 * By default, if no ViewResolvers have been configured, a View that doesn't do anything is used.
	 * 
	 * <p>Most ViewResolver types should work as expected. This excludes {@link BeanNameViewResolver}
	 * since there is no ApplicationContext.
	 * 
	 */
	public StandaloneServerBuilder setViewResolvers(ViewResolver...resolvers) {
		viewResolvers = Arrays.asList(resolvers);
		return this;
	}
	
	@Override
	protected WebApplicationContext initApplicationContext() {
		applicationContext = new GenericWebApplicationContext(new MockServletContext());
		return applicationContext;
	}

	@Override
	protected List<? extends HandlerMapping> initHandlerMappings() {
		StaticRequestMappingHandlerMapping mapping = new StaticRequestMappingHandlerMapping();
		mapping.registerHandlers(controllers);
		mapping.setInterceptors(mappedInterceptors.toArray());
		return Collections.singletonList(mapping);
	}

	@Override
	protected List<? extends HandlerAdapter> initHandlerAdapters() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		if (messageConverters != null) {
			adapter.setMessageConverters(messageConverters);
		}
		
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(conversionService);
		initializer.setValidator(validator);
		adapter.setWebBindingInitializer(initializer);
		
		adapter.setApplicationContext(applicationContext);	// for SpEL expressions in annotations
		adapter.afterPropertiesSet();
		
		return Collections.singletonList(adapter);
	}

	@Override
	protected List<? extends HandlerExceptionResolver> initHandlerExceptionResolvers() {
		ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
		if (messageConverters != null) {
			exceptionResolver.setMessageConverters( messageConverters);
		}
		exceptionResolver.afterPropertiesSet();
		
		List<HandlerExceptionResolver> resolvers = new ArrayList<HandlerExceptionResolver>();
		resolvers.add(exceptionResolver);
		resolvers.add(new ResponseStatusExceptionResolver());
		resolvers.add(new DefaultHandlerExceptionResolver());

		return resolvers;
	}

	@Override
	protected List<? extends ViewResolver> initViewResolvers() {
		viewResolvers = (viewResolvers == null) ? 
				Arrays.asList(new FixedViewResolver(NOOP_VIEW)) : viewResolvers;
				
		for (Object vr : viewResolvers) {
			if (vr instanceof ApplicationContextAware) {
				((ApplicationContextAware) vr).setApplicationContext(applicationContext);
			}
		}

		return viewResolvers;
	}

	@Override
	protected RequestToViewNameTranslator initViewNameTranslator() {
		return new DefaultRequestToViewNameTranslator();
	}

	@Override
	protected LocaleResolver initLocaleResolver() {
		return new AcceptHeaderLocaleResolver();
	}

	/**
	 * Allows registering controller instances.
	 */
	private static class StaticRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
		
		public void registerHandlers(Object...handlers) {
			for (Object handler : handlers) {
				super.detectHandlerMethods(handler);
			}
		}
	}

	/**
	 * Resolves all view names to the same fixed View.
	 */
	private static class FixedViewResolver implements ViewResolver {
		
		private final View view;
		
		public FixedViewResolver(View view) {
			this.view = view;
		}

		public View resolveViewName(String viewName, Locale locale) throws Exception {
			return view;
		}
	}
	
	/**
	 * A View implementation that doesn't do anything.
	 */
	private static final View NOOP_VIEW = new View() {

		public String getContentType() {
			return null;
		}

		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
		}
	};

}
