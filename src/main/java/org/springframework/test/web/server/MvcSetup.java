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

import org.springframework.web.servlet.FlashMapManager;
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
	 * Return HandlerMappings to use to map requests.
	 */
	List<HandlerMapping> getHandlerMappings();

	/**
	 * Return HandlerAdapters to use to invoke handlers.
	 */
	List<HandlerAdapter> getHandlerAdapters();

	/**
	 * Return HandlerExceptionResolvers to use to resolve controller exceptions.
	 */
	List<HandlerExceptionResolver> getExceptionResolvers();

	/**
	 * Return ViewResolvers to use to resolve view names.
	 */
	List<ViewResolver> getViewResolvers();

	/**
	 * Return RequestToViewNameTranslator to use to derive a view name.
	 */
	RequestToViewNameTranslator getViewNameTranslator();

	/**
	 * Return LocaleResolver to use for locale resolution.
	 */
	LocaleResolver getLocaleResolver();

	/**
	 * Return FlashMapManager to use for flash attribute support.
	 */
	FlashMapManager getFlashMapManager();

}
