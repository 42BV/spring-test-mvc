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

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcSetup;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;

/**
 * An abstract class for building {@link MockMvc} instances.
 *
 * @author Rossen Stoyanchev
 */
public abstract class AbstractMockMvcBuilder {

	/**
	 * Build a {@link MockMvc} instance.
	 */
	public final MockMvc build() {

		ServletContext servletContext = initServletContext();
		WebApplicationContext wac = initWebApplicationContext(servletContext);
		if (wac != null) {
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		}

		final List<HandlerMapping> handlerMappings = initHandlerMappings(wac);
		final List<HandlerAdapter> handlerAdapters = initHandlerAdapters(wac);
		final List<HandlerExceptionResolver> exceptionResolvers = initHandlerExceptionResolvers(wac);
		final List<ViewResolver> viewResolvers = initViewResolvers(wac);
		final RequestToViewNameTranslator viewNameTranslator = initViewNameTranslator(wac);
		final LocaleResolver localeResolver = initLocaleResolver(wac);
		
		MvcSetup mvcSetup = new MvcSetup() {

			public List<HandlerMapping> getHandlerMappings() {
				return Collections.unmodifiableList(handlerMappings);
			}

			public List<HandlerAdapter> getHandlerAdapters() {
				return Collections.unmodifiableList(handlerAdapters);
			}

			public List<ViewResolver> getViewResolvers() {
				return Collections.unmodifiableList(viewResolvers);
			}

			public List<HandlerExceptionResolver> getExceptionResolvers() {
				return Collections.unmodifiableList(exceptionResolvers);
			}

			public RequestToViewNameTranslator getViewNameTranslator() {
				return viewNameTranslator;
			}

			public LocaleResolver getLocaleResolver() {
				return localeResolver;
			}
		};
		
		return new MockMvc(servletContext, mvcSetup) {};
	}

	/**
	 * Return ServletContext to use, never "null".
	 */
	protected abstract ServletContext initServletContext();

	/**
	 * Return the WebApplicationContext to use, may be "null".
	 */
	protected abstract WebApplicationContext initWebApplicationContext(ServletContext servletContext);

	/**
	 * Return the {@link HandlerMapping}s to use to map requests, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract List<HandlerMapping> initHandlerMappings(WebApplicationContext wac);

	/**
	 * Return the {@link HandlerAdapter}s to use to invoke handlers, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract List<HandlerAdapter> initHandlerAdapters(WebApplicationContext wac);

	/**
	 * Return the {@link HandlerExceptionResolver}s to use to resolve controller exceptions, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract List<HandlerExceptionResolver> initHandlerExceptionResolvers(WebApplicationContext wac);

	/**
	 * Return the {@link ViewResolver}s to use to resolve view names, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract List<ViewResolver> initViewResolvers(WebApplicationContext wac);

	/**
	 * Return the {@link RequestToViewNameTranslator} to use to derive a view name, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract RequestToViewNameTranslator initViewNameTranslator(WebApplicationContext wac);

	/**
	 * Return the {@link LocaleResolver} to use for locale resolution, never "null".
	 * @param wac the fully initialized Spring application context
	 */
	protected abstract LocaleResolver initLocaleResolver(WebApplicationContext wac);

}
