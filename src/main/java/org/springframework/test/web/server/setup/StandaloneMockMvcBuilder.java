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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.server.MockMvc;
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
 * Build a {@link MockMvc} by directly instantiating required Spring MVC components rather 
 * than scanning a Spring ApplicationContext. This may be preferable when you want to build 
 * very focused tests involving just one or a few controllers.
 * 
 * <p>The resulting setup aims to support @{@link RequestMapping} methods using default 
 * configuration, similar to the MVC namespace, with various customizations possible. 
 * 
 * <p>View resolution can be configured via {@link #setViewResolvers} or 
 * {@link #setFixedView(View)}. Or if view resolution is not configured, 
 * a fixed {@link View} that doesn't render anything is used.
 * 
 * @author Rossen Stoyanchev
 */ 
public class StandaloneMockMvcBuilder extends AbstractMockMvcBuilder {
	
	private final Object[] controllers;
	
	private List<HttpMessageConverter<?>> messageConverters;
	
	private Validator validator;
	
	private FormattingConversionService conversionService = new DefaultFormattingConversionService();
	
	private final List<MappedInterceptor> mappedInterceptors = new ArrayList<MappedInterceptor>();

	private List<ViewResolver> viewResolvers;

	/**
	 * Create an instance registering @{@link RequestMapping} methods from the given controllers.
	 */
	public StandaloneMockMvcBuilder(Object[] controllers) {
		Assert.isTrue(!ObjectUtils.isEmpty(controllers), "At least one controller is required");
		this.controllers = controllers;
	}

	public StandaloneMockMvcBuilder setMessageConverters(HttpMessageConverter<?>...messageConverters) {
		this.messageConverters = Arrays.asList(messageConverters);
		return this;
	}

	public StandaloneMockMvcBuilder setValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	public StandaloneMockMvcBuilder setConversionService(FormattingConversionService conversionService) {
		this.conversionService = conversionService;
		return this;
	}
	
	public StandaloneMockMvcBuilder addInterceptors(HandlerInterceptor... interceptors) {
		mapInterceptors(null, interceptors);
		return this;
	}

	public StandaloneMockMvcBuilder mapInterceptors(String[] pathPatterns, HandlerInterceptor... interceptors) {
		for (HandlerInterceptor interceptor : interceptors) {
			this.mappedInterceptors.add(new MappedInterceptor(pathPatterns, interceptor));
		}
		return this;
	}
	
	/**
	 * Sets up a single {@link ViewResolver} that always returns the provided view -
	 * a convenient shortcut to rendering generated content (e.g. JSON, XML, Atom, etc.) 
	 * For URL-based views, use {@link #setViewResolvers(ViewResolver...)} instead.
	 * 
	 * @param view the default View to return for any view name
	 */
	public StandaloneMockMvcBuilder setFixedView(View view) {
		this.viewResolvers = Collections.<ViewResolver>singletonList(new FixedViewResolver(view));
		return this;
	}

	/**
	 * Set up view resolution with the given {@link ViewResolver}s. If this property is
	 * not used, a fixed, noop View is used instead.
	 * 
	 * <p>If you need to use a {@link BeanNameViewResolver}, use {@link AbstractContextMockMvcBuilder} instead.
	 */
	public StandaloneMockMvcBuilder setViewResolvers(ViewResolver...resolvers) {
		this.viewResolvers = Arrays.asList(resolvers);
		return this;
	}
	
	@Override
	protected ServletContext initServletContext() {
		return new MockServletContext();
	}	
	
	@Override
	protected WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		GenericWebApplicationContext wac = new GenericWebApplicationContext(servletContext);
		wac.refresh();
		wac.getAutowireCapableBeanFactory().initializeBean(this.validator, "validator");
		return wac;
	}

	@Override
	protected List<HandlerMapping> initHandlerMappings(WebApplicationContext wac) {
		StaticRequestMappingHandlerMapping mapping = new StaticRequestMappingHandlerMapping();
		mapping.registerHandlers(this.controllers);
		mapping.setInterceptors(this.mappedInterceptors.toArray());
		return Collections.<HandlerMapping>singletonList(mapping);
	}

	@Override
	protected List<HandlerAdapter> initHandlerAdapters(WebApplicationContext wac) {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		if (this.messageConverters != null) {
			adapter.setMessageConverters(this.messageConverters);
		}
		
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(this.conversionService);
		initializer.setValidator(this.validator);
		adapter.setWebBindingInitializer(initializer);
		
		adapter.setApplicationContext(wac);	// for SpEL expressions in annotations
		adapter.afterPropertiesSet();
		
		return Collections.<HandlerAdapter>singletonList(adapter);
	}

	@Override
	protected List<HandlerExceptionResolver> initHandlerExceptionResolvers(WebApplicationContext wac) {
		ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
		if (this.messageConverters != null) {
			exceptionResolver.setMessageConverters(this.messageConverters);
		}
		exceptionResolver.afterPropertiesSet();
		
		List<HandlerExceptionResolver> resolvers = new ArrayList<HandlerExceptionResolver>();
		resolvers.add(exceptionResolver);
		resolvers.add(new ResponseStatusExceptionResolver());
		resolvers.add(new DefaultHandlerExceptionResolver());

		return resolvers;
	}

	@Override
	protected List<ViewResolver> initViewResolvers(WebApplicationContext wac) {
		this.viewResolvers = (this.viewResolvers == null) ? 
				Arrays.<ViewResolver>asList(new FixedViewResolver(NOOP_VIEW)) : viewResolvers;
				
		for (Object viewResolver : this.viewResolvers) {
			if (viewResolver instanceof ApplicationContextAware) {
				((ApplicationContextAware) viewResolver).setApplicationContext(wac);
			}
		}

		return this.viewResolvers;
	}

	@Override
	protected RequestToViewNameTranslator initViewNameTranslator(WebApplicationContext wac) {
		return new DefaultRequestToViewNameTranslator();
	}

	@Override
	protected LocaleResolver initLocaleResolver(WebApplicationContext wac) {
		return new AcceptHeaderLocaleResolver();
	}

	/**
	 * A {@link RequestMappingHandlerMapping} allowing direct registration of controller 
	 * instances rather than scanning a WebApplicationContext.
	 */
	private static class StaticRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
		
		public void registerHandlers(Object...handlers) {
			for (Object handler : handlers) {
				super.detectHandlerMethods(handler);
			}
		}
	}

	/**
	 * A {@link ViewResolver} that always returns same View.
	 */
	private static class FixedViewResolver implements ViewResolver {
		
		private final View view;
		
		public FixedViewResolver(View view) {
			this.view = view;
		}

		public View resolveViewName(String viewName, Locale locale) throws Exception {
			return this.view;
		}
	}
	
	/**
	 * A {@link View} that does not render.
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
