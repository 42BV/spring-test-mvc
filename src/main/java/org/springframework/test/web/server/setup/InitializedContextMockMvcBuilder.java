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

import javax.servlet.ServletContext;

import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * A MockMvcBuilder that discovers controllers and Spring MVC infrastructure
 * components in a WebApplicationContext.
 *
 * @author Rossen Stoyanchev
 */
public class InitializedContextMockMvcBuilder extends AbstractMockMvcBuilder {

	private final WebApplicationContext applicationContext;

	/**
     * Protected constructor. Not intended for direct instantiation.
     * @see MockMvcBuilders#webApplicationContextSetup(WebApplicationContext)
	 */
	protected InitializedContextMockMvcBuilder(WebApplicationContext wac) {
		Assert.notNull(wac, "WebApplicationContext is required");
		Assert.notNull(wac.getServletContext(), "WebApplicationContext must have a ServletContext");
		this.applicationContext = wac;
	}

	@Override
	protected ServletContext initServletContext() {
		return this.applicationContext.getServletContext();
	}

	@Override
	protected WebApplicationContext initWebApplicationContext(ServletContext context) {
		return this.applicationContext;
	}

}
