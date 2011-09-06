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

package org.springframework.test.web.server;

import java.util.List;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;

/**
 * Provides {@link MockMvc} with access to Spring MVC infrastructure components.
 * 
 * @author Rossen Stoyanchev
 */
public interface MvcSetup {

	/**
	 * Return the {@link HandlerMapping}s to use to map requests, never "null".
	 */
	List<HandlerMapping> getHandlerMappings();

	/**
	 * Return the {@link HandlerAdapter}s to use to invoke handlers, never "null".
	 */
	List<HandlerAdapter> getHandlerAdapters();

	/**
	 * Return the {@link HandlerExceptionResolver}s to use to resolve controller exceptions, never "null".
	 */
	List<HandlerExceptionResolver> getExceptionResolvers();

	/**
	 * Return the {@link ViewResolver}s to use to resolve view names, never "null".
	 */
	List<ViewResolver> getViewResolvers();

	/**
	 * Return the {@link RequestToViewNameTranslator} to use to derive a view name, never "null".
	 */
	RequestToViewNameTranslator getViewNameTranslator();

	/**
	 * Return the {@link LocaleResolver} to use for locale resolution, never "null".
	 */
	LocaleResolver getLocaleResolver();

}
