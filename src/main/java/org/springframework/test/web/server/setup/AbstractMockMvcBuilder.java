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
import org.springframework.web.servlet.FlashMapManager;
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
		final FlashMapManager flashMapManager = initFlashMapManager(wac);
		
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

			public FlashMapManager getFlashMapManager() {
				return flashMapManager;
			}
		};

		mvcSetupInitialized(mvcSetup, servletContext, wac);

		return new MockMvc(servletContext, mvcSetup) {};
	}

	/**
	 * Return ServletContext to use, never {@code null}.
	 */
	protected abstract ServletContext initServletContext();

	/**
	 * Return the WebApplicationContext to use, possibly {@code null}.
	 * @param servletContext the ServletContext returned 
	 * from {@link #initServletContext()}
	 */
	protected abstract WebApplicationContext initWebApplicationContext(ServletContext servletContext);

	/**
	 * Return the HandlerMappings to use to map requests.
	 * @param wac the fully initialized Spring application context
	 * @return a List of HandlerMapping types or an empty list.
	 */
	protected abstract List<HandlerMapping> initHandlerMappings(WebApplicationContext wac);

	/**
	 * Return the HandlerAdapters to use to invoke handlers.
	 * @param wac the fully initialized Spring application context
	 * @return a List of HandlerExceptionResolver types or an empty list.
	 */
	protected abstract List<HandlerAdapter> initHandlerAdapters(WebApplicationContext wac);

	/**
	 * Return HandlerExceptionResolvers for resolving controller exceptions.
	 * @param wac the fully initialized Spring application context
	 * @return a List of HandlerExceptionResolver types or an empty list.
	 */
	protected abstract List<HandlerExceptionResolver> initHandlerExceptionResolvers(WebApplicationContext wac);

	/**
	 * Return the ViewResolvers to use to resolve view names.
	 * @param wac the fully initialized Spring application context
	 * @return a List of ViewResolver types or an empty list.
	 */
	protected abstract List<ViewResolver> initViewResolvers(WebApplicationContext wac);

	/**
	 * Return the RequestToViewNameTranslator to use to derive a view name
	 * @param wac the fully initialized Spring application context
	 * @return a RequestToViewNameTranslator, never {@code null}
	 */
	protected abstract RequestToViewNameTranslator initViewNameTranslator(WebApplicationContext wac);

	/**
	 * Return the LocaleResolver to use for locale resolution.
	 * @param wac the fully initialized Spring application context
	 * @return a LocaleResolver, never {@code null}
	 */
	protected abstract LocaleResolver initLocaleResolver(WebApplicationContext wac);

	/**
	 * Return the FlashMapManager to use for flash attribute support.
	 * @param wac the fully initialized Spring application context
	 * @return a FlashMapManager, never {@code null}
	 */
	protected abstract FlashMapManager initFlashMapManager(WebApplicationContext wac);

	/**
	 * A hook for sub-classes providing access to the initialized MvcSetup, 
	 * ServletContext, and WebApplicationContext.
	 */
	protected void mvcSetupInitialized(MvcSetup mvcSetup, ServletContext servletContext, WebApplicationContext wac) {
	}

}
