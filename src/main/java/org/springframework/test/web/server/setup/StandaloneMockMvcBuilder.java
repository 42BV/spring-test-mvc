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

import javax.servlet.ServletContext;
import javax.xml.transform.Source;

import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Build a {@link MockMvc} by directly instantiating required Spring MVC components rather 
 * than scanning a Spring ApplicationContext. This may be preferable when you want to build 
 * very focused tests involving just one or a few controllers.
 * 
 * <p>The resulting setup aims to support @{@link RequestMapping} methods using default 
 * configuration, similar to the MVC namespace, with various customizations possible. 
 * 
 * <p>View resolution can be configured via {@link #setViewResolvers} or 
 * {@link #setSingleView(View)}. Or if view resolution is not configured, 
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

    private List<HandlerMethodArgumentResolver> argumentResolvers;

	/**
     * Protected constructor. Not intended for direct instantiation.
     * @see MockMvcBuilders#standaloneSetup(Object...)
	 */
	protected StandaloneMockMvcBuilder(Object[] controllers) {
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
	 * Sets up a single {@link ViewResolver} that always returns the provided 
	 * view instance. This is a convenient shortcut if you need to use one 
	 * View instance only -- e.g. rendering generated content (JSON, XML, Atom).
	 * @param view the View instance to return every time
	 */
	public StandaloneMockMvcBuilder setSingleView(View view) {
		this.viewResolvers = Collections.<ViewResolver>singletonList(new StubViewResolver(view));
		return this;
	}

	/**
	 * Set up view resolution with the given {@link ViewResolver}s. If this property is
	 * not used, a fixed, noop View is used instead.
	 * 
	 * <p>If you need to use a {@link BeanNameViewResolver}, use {@link ContextMockMvcBuilderSupport} instead.
	 */
	public StandaloneMockMvcBuilder setViewResolvers(ViewResolver...resolvers) {
		this.viewResolvers = Arrays.asList(resolvers);
		return this;
	}

    public StandaloneMockMvcBuilder setArgumentResolvers(HandlerMethodArgumentResolver... resolvers) {
        this.argumentResolvers = Arrays.asList(resolvers);
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
		StaticRequestMappingHandlerMapping handlerMapping = new StaticRequestMappingHandlerMapping();
		handlerMapping.registerHandlers(this.controllers);
		handlerMapping.setInterceptors(this.mappedInterceptors.toArray());
		handlerMapping.setOrder(0);
		return Collections.<HandlerMapping>singletonList(handlerMapping);
	}

	@Override
	protected List<HandlerAdapter> initHandlerAdapters(WebApplicationContext wac) {
		if (this.messageConverters == null) {
			this.messageConverters = getDefaultHttpMessageConverters();
		}
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(this.conversionService);
		initializer.setValidator(this.validator);

		RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
		handlerAdapter.setWebBindingInitializer(initializer);
		handlerAdapter.setMessageConverters(this.messageConverters);
        handlerAdapter.setCustomArgumentResolvers(this.argumentResolvers);
		handlerAdapter.setApplicationContext(wac);	// for SpEL expressions in annotations
		handlerAdapter.afterPropertiesSet();
		
		return Collections.<HandlerAdapter>singletonList(handlerAdapter);
	}

	/**
	 * Override this method to add default {@link HttpMessageConverter}s. 
	 * @param messageConverters the list to add the default message converters to
	 */
	private List<HttpMessageConverter<?>> getDefaultHttpMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setWriteAcceptCharset(false);

		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new XmlAwareFormHttpMessageConverter());

		ClassLoader classLoader = getClass().getClassLoader();
		if (ClassUtils.isPresent("javax.xml.bind.Binder", classLoader)) {
			messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}
		if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", classLoader)) {
			messageConverters.add(new MappingJacksonHttpMessageConverter());
		}
		if (ClassUtils.isPresent("com.sun.syndication.feed.WireFeed", classLoader)) {
			messageConverters.add(new AtomFeedHttpMessageConverter());
			messageConverters.add(new RssChannelHttpMessageConverter());
		}
		
		return messageConverters;
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
				Arrays.<ViewResolver>asList(new InternalResourceViewResolver()) : viewResolvers;
				
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
	private static class StubViewResolver implements ViewResolver {
		
		private final View view;
		
		public StubViewResolver(View view) {
			this.view = view;
		}

		public View resolveViewName(String viewName, Locale locale) throws Exception {
			return this.view;
		}
	}

}
