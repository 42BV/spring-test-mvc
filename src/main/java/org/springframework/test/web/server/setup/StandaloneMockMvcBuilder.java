/*
 * Copyright 2011-2012 the original author or authors.
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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * A MockMvcBuilder that accepts registrations of controller instances rather
 * than searching for them in a Spring ApplicationContext. This allows full
 * control over the instantiation and the initialization of controllers and
 * their dependencies similar to plain unit tests.
 *
 * <p>This MockMvcBuilder also instantiates the minimum set of Spring MVC
 * infrastructure components required for the processing of requests with
 * annotated controllers. The set of infrastructure components is very similar
 * to that provided by the MVC namespace or the MVC Java config.
 * A number of properties in this class can be used to customize the provided
 * configuration.
 *
 * <p>View resolution can be configured either by selecting a "fixed" view to
 * use (see {@link #setSingleView(View)}) or by providing a list of
 * ViewResolver types (see {@link #setViewResolvers(ViewResolver...)}).
 *
 * @author Rossen Stoyanchev
 */
public class StandaloneMockMvcBuilder extends AbstractMockMvcBuilder<StandaloneMockMvcBuilder> {

	private final Object[] controllers;

	private List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

	private List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();

	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();

	private final List<MappedInterceptor> mappedInterceptors = new ArrayList<MappedInterceptor>();

	private Validator validator = null;

	private FormattingConversionService conversionService = null;

	private List<HandlerExceptionResolver> handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>();

	private List<ViewResolver> viewResolvers;

	private LocaleResolver localeResolver = new AcceptHeaderLocaleResolver();

	private FlashMapManager flashMapManager = null;

	private boolean useSuffixPatternMatch = true;

	private boolean useTrailingSlashPatternMatch = true;

	/**
	 * Protected constructor. Not intended for direct instantiation.
	 * @see MockMvcBuilders#standaloneSetup(Object...)
	 */
	protected StandaloneMockMvcBuilder(Object... controllers) {
		Assert.isTrue(!ObjectUtils.isEmpty(controllers), "At least one controller is required");
		this.controllers = controllers;
	}

	/**
	 * Set the message converters to use in argument resolvers and in return value
	 * handlers, which support reading and/or writing to the body of the request
	 * and response. If no message converters are added to the list, a default
	 * list of converters is added instead.
	 */
	public StandaloneMockMvcBuilder setMessageConverters(HttpMessageConverter<?>...messageConverters) {
		this.messageConverters = Arrays.asList(messageConverters);
		return this;
	}

	/**
	 * Provide a custom {@link Validator} instead of the one created by default.
	 * The default implementation used, assuming JSR-303 is on the classpath, is
	 * {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean}.
	 */
	public StandaloneMockMvcBuilder setValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	/**
	 * Provide a conversion service with custom formatters and converters.
	 * If not set, a {@link DefaultFormattingConversionService} is used by default.
	 */
	public StandaloneMockMvcBuilder setConversionService(FormattingConversionService conversionService) {
		this.conversionService = conversionService;
		return this;
	}

	/**
	 * Add interceptors mapped to all incoming requests.
	 */
	public StandaloneMockMvcBuilder addInterceptors(HandlerInterceptor... interceptors) {
		addMappedInterceptors(null, interceptors);
		return this;
	}

	/**
	 * Add interceptors mapped to a set of path patterns.
	 */
	public StandaloneMockMvcBuilder addMappedInterceptors(String[] pathPatterns, HandlerInterceptor... interceptors) {
		for (HandlerInterceptor interceptor : interceptors) {
			this.mappedInterceptors.add(new MappedInterceptor(pathPatterns, interceptor));
		}
		return this;
	}

	/**
	 * Provide custom resolvers for controller method arguments.
	 */
	public StandaloneMockMvcBuilder setCustomArgumentResolvers(HandlerMethodArgumentResolver... argumentResolvers) {
		this.customArgumentResolvers = Arrays.asList(argumentResolvers);
		return this;
	}

	/**
	 * Provide custom handlers for controller method return values.
	 */
	public StandaloneMockMvcBuilder setCustomReturnValueHandlers(HandlerMethodReturnValueHandler... handlers) {
		this.customReturnValueHandlers = Arrays.asList(handlers);
		return this;
	}


	/**
	 * Set the HandlerExceptionResolver types to use.
	 */
	public void setHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		this.handlerExceptionResolvers = exceptionResolvers;
	}

	/**
	 * Set up view resolution with the given {@link ViewResolver}s.
	 * If not set, an {@link InternalResourceViewResolver} is used by default.
	 */
	public StandaloneMockMvcBuilder setViewResolvers(ViewResolver...resolvers) {
		this.viewResolvers = Arrays.asList(resolvers);
		return this;
	}

	/**
	 * Sets up a single {@link ViewResolver} that always returns the provided
	 * view instance. This is a convenient shortcut if you need to use one
	 * View instance only -- e.g. rendering generated content (JSON, XML, Atom).
	 */
	public StandaloneMockMvcBuilder setSingleView(View view) {
		this.viewResolvers = Collections.<ViewResolver>singletonList(new StubViewResolver(view));
		return this;
	}

	/**
	 * Provide a LocaleResolver instance.
	 * If not provided, the default one used is {@link AcceptHeaderLocaleResolver}.
	 */
	public StandaloneMockMvcBuilder setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
		return this;
	}

	/**
	 * Provide a custom FlashMapManager instance.
	 * If not provided, {@code SessionFlashMapManager} is used by default.
	 */
	public StandaloneMockMvcBuilder setFlashMapManager(FlashMapManager flashMapManager) {
		this.flashMapManager = flashMapManager;
		return this;
	}

	/**
	 * Whether to use suffix pattern match (".*") when matching patterns to
	 * requests. If enabled a method mapped to "/users" also matches to "/users.*".
	 * <p>The default value is {@code true}.
	 */
	public StandaloneMockMvcBuilder setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
		this.useSuffixPatternMatch = useSuffixPatternMatch;
		return this;
	}

	/**
	 * Whether to match to URLs irrespective of the presence of a trailing slash.
	 * If enabled a method mapped to "/users" also matches to "/users/".
	 * <p>The default value is {@code true}.
	 */
	public StandaloneMockMvcBuilder setUseTrailingSlashPatternMatch(boolean useTrailingSlashPatternMatch) {
		this.useTrailingSlashPatternMatch = useTrailingSlashPatternMatch;
		return this;
	}

	@Override
	protected ServletContext initServletContext() {
		return new MockServletContext();
	}

	@Override
	protected WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		StubWebApplicationContext wac = new StubWebApplicationContext(servletContext);
		registerMvcSingletons(wac);
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		return wac;
	}

	private void registerMvcSingletons(StubWebApplicationContext wac) {
		WebMvcConfig config = new WebMvcConfig();

		RequestMappingHandlerMapping handlerMapping = config.requestMappingHandlerMapping();
		extendRequestMappingHandlerMapping(handlerMapping);
		handlerMapping.setServletContext(wac.getServletContext());
		handlerMapping.setApplicationContext(wac);
		wac.addBean("requestMappingHandlerMapping", handlerMapping);

		RequestMappingHandlerAdapter handlerAdapter = config.requestMappingHandlerAdapter();
		extendRequestMappingHandlerAdapter(handlerAdapter);
		handlerAdapter.setServletContext(wac.getServletContext());
		handlerAdapter.setApplicationContext(wac);
		handlerAdapter.afterPropertiesSet();
		wac.addBean("requestMappingHandlerAdapter", handlerAdapter);

		wac.addBean("handlerExceptionResolver", config.handlerExceptionResolver());

		wac.addBeans(initViewResolvers(wac));
		wac.addBean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, this.localeResolver);
		wac.addBean(DispatcherServlet.THEME_RESOLVER_BEAN_NAME, new FixedThemeResolver());
		wac.addBean(DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, new DefaultRequestToViewNameTranslator());

		if (this.flashMapManager == null) {
			initFlashMapManager();
		}
		wac.addBean(DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME, this.flashMapManager);
	}

	private void initFlashMapManager() {
		String className = "org.springframework.web.servlet.support.DefaultFlashMapManager";
		if (ClassUtils.isPresent(className, getClass().getClassLoader())) {
			this.flashMapManager = instantiateClass(className);
		}
		else {
			className = "org.springframework.web.servlet.support.SessionFlashMapManager";
			this.flashMapManager = instantiateClass(className);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T instantiateClass(String className) {
		Class<?> clazz;
		try {
			clazz = ClassUtils.forName(className, StandaloneMockMvcBuilder.class.getClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new BeanInitializationException("Could not instantiate " + className, e);
		}
		catch (LinkageError e) {
			throw new BeanInitializationException("Could not instantiate " + className, e);
		}
		return (T) BeanUtils.instantiate(clazz);
	}

	/**
	 * Allows sub-classes to customize the RequestMappingHandlerMapping instance.
	 */
	protected void extendRequestMappingHandlerMapping(RequestMappingHandlerMapping handlerMapping) {
	}

	/**
	 * Allows sub-classes to customize the RequestMappingHandlerAdapter instance.
	 */
	protected void extendRequestMappingHandlerAdapter(RequestMappingHandlerAdapter handlerAdapter) {
	}

	private List<ViewResolver> initViewResolvers(WebApplicationContext wac) {
		this.viewResolvers = (this.viewResolvers == null) ?
				Arrays.<ViewResolver>asList(new InternalResourceViewResolver()) : viewResolvers;

		for (Object viewResolver : this.viewResolvers) {
			if (viewResolver instanceof WebApplicationObjectSupport) {
				((WebApplicationObjectSupport) viewResolver).setApplicationContext(wac);
			}
		}

		return this.viewResolvers;
	}


	/**
	 * A sub-class of {@link WebMvcConfigurationSupport} that allows re-using
	 * the MVC Java config setup with customizations for the "standalone" setup.
	 */
	private class WebMvcConfig extends WebMvcConfigurationSupport {

		@Override
		public RequestMappingHandlerMapping requestMappingHandlerMapping() {
			StaticRequestMappingHandlerMapping handlerMapping = new StaticRequestMappingHandlerMapping();
			handlerMapping.setUseSuffixPatternMatch(useSuffixPatternMatch);
			handlerMapping.setUseTrailingSlashMatch(useTrailingSlashPatternMatch);
			handlerMapping.registerHandlers(StandaloneMockMvcBuilder.this.controllers);
			handlerMapping.setOrder(0);
			handlerMapping.setInterceptors(getInterceptors());
			return handlerMapping;
		}

		@Override
		protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			converters.addAll(StandaloneMockMvcBuilder.this.messageConverters);
		}

		@Override
		protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
			argumentResolvers.addAll(StandaloneMockMvcBuilder.this.customArgumentResolvers);
		}

		@Override
		protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
			returnValueHandlers.addAll(StandaloneMockMvcBuilder.this.customReturnValueHandlers);
		}

		@Override
		protected void addInterceptors(InterceptorRegistry registry) {
			for (MappedInterceptor interceptor : StandaloneMockMvcBuilder.this.mappedInterceptors) {
				InterceptorRegistration registration = registry.addInterceptor(interceptor.getInterceptor());
				if (interceptor.getPathPatterns() != null) {
					registration.addPathPatterns(interceptor.getPathPatterns());
				}
			}
		}

		@Override
		public FormattingConversionService mvcConversionService() {
			FormattingConversionService mvcConversionService = (StandaloneMockMvcBuilder.this.conversionService != null) ?
					StandaloneMockMvcBuilder.this.conversionService : super.mvcConversionService();
			return (mvcConversionService == null) ? super.mvcConversionService() : mvcConversionService;
		}

		@Override
		public Validator mvcValidator() {
			Validator mvcValidator = (StandaloneMockMvcBuilder.this.validator != null) ?
					StandaloneMockMvcBuilder.this.validator : super.mvcValidator();
			if (mvcValidator instanceof InitializingBean) {
				try {
					((InitializingBean) mvcValidator).afterPropertiesSet();
				}
				catch (Exception e) {
					throw new BeanInitializationException("Failed to initialize Validator", e);
				}
			}
			return mvcValidator;
		}

		@Override
		protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
			exceptionResolvers.addAll(StandaloneMockMvcBuilder.this.handlerExceptionResolvers);
		}
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
