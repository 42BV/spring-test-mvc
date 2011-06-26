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

import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * Expects a fully initialized {@link WebApplicationContext}.
 * 
 */
public class InitializedContextMockMvcBuilder extends AbstractContextMockMvcBuilder {

	private final WebApplicationContext applicationContext;
	
	public InitializedContextMockMvcBuilder(WebApplicationContext context) {
		Assert.notNull(context, "WebApplicationContext is required");
		Assert.notNull(context.getServletContext(), "WebApplicationContext must have a ServletContext");
		this.applicationContext = context;
	}

	@Override
	protected WebApplicationContext initApplicationContext() {
		return this.applicationContext;
	}

	@Override
	protected WebApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

}
