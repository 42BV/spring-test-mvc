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
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.OrderComparator;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * An abstract class for {@link MockMvc} builders that find Spring MVC components by looking 
 * them up in a Spring {@link WebApplicationContext}.
 * 
 * @author Rossen Stoyanchev
 */
public abstract class AbstractContextMockMvcBuilder extends AbstractMockMvcBuilder {

	@Override
	protected List<HandlerMapping> initHandlerMappings(WebApplicationContext wac) {
		List<HandlerMapping> result = getOrderedBeans(wac, HandlerMapping.class);
		if (result.isEmpty()) {
			result.add(new BeanNameUrlHandlerMapping());
			result.add(new DefaultAnnotationHandlerMapping());
		}
		return result;
	}
	
	@Override
	protected List<HandlerAdapter> initHandlerAdapters(WebApplicationContext wac) {
		List<HandlerAdapter> result = getOrderedBeans(wac, HandlerAdapter.class);
		if (result.isEmpty()) {
			result.add(new HttpRequestHandlerAdapter());
			result.add(new SimpleControllerHandlerAdapter());
			result.add(new AnnotationMethodHandlerAdapter());
		}
		return result;
	}
	
	@Override
	protected List<HandlerExceptionResolver> initHandlerExceptionResolvers(WebApplicationContext wac) {
		List<HandlerExceptionResolver> result = getOrderedBeans(wac, HandlerExceptionResolver.class);
		if (result.isEmpty()) {
			result.add(new AnnotationMethodHandlerExceptionResolver());
			result.add(new ResponseStatusExceptionResolver());
			result.add(new DefaultHandlerExceptionResolver());
		}
		return result;
	}

	@Override
	protected List<ViewResolver> initViewResolvers(WebApplicationContext wac) {
		List<ViewResolver> result = getOrderedBeans(wac, ViewResolver.class);
		if (result.isEmpty()) {
			result.add(new InternalResourceViewResolver());
		}
		return result;
	}

	private <T> List<T> getOrderedBeans(WebApplicationContext wac, Class<T> beanType) {
		List<T> components = new ArrayList<T>();
		Map<String, T> beans =
			BeanFactoryUtils.beansOfTypeIncludingAncestors(wac, beanType, true, false);
		if (!beans.isEmpty()) {
			components.addAll(beans.values());
			OrderComparator.sort(components);
		}
		return components;
	}

	@Override
	protected RequestToViewNameTranslator initViewNameTranslator(WebApplicationContext wac) {
		String name = DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME;
		return getBeanByName(wac, name, RequestToViewNameTranslator.class, DefaultRequestToViewNameTranslator.class);
	}

	@Override
	protected LocaleResolver initLocaleResolver(WebApplicationContext wac) {
		String name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME;
		return getBeanByName(wac, name, LocaleResolver.class, AcceptHeaderLocaleResolver.class);
	}

	private <T> T getBeanByName(WebApplicationContext wac, String name, Class<T> requiredType, Class<? extends T> defaultType) {
		try {
			return wac.getBean(name, requiredType);
		}
		catch (NoSuchBeanDefinitionException ex) {
			return (defaultType != null) ? BeanUtils.instantiate(defaultType) : null;
		}
	}
	
}
