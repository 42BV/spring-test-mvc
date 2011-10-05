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

import javax.servlet.ServletContext;

import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link MockMvc} builder that expects a fully initialized {@link WebApplicationContext}
 * and looks up Spring MVC components in it.
 * 
 * @author Rossen Stoyanchev
 */
public class InitializedContextMockMvcBuilder extends ContextMockMvcBuilderSupport {

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
