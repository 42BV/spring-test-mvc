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

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * <strong>Main entry point for server-side Spring MVC test support.</strong>
 * 
 */
public class MockMvc {

	private final ServletContext servletContext;
	
	private final MockDispatcher mockDispatcher;

	private boolean mapOnly;
	
	/**
	 * To create a {@link MockMvc} instance see methods in {@code MockMvcBuilders}.
	 */
	private MockMvc(ServletContext servletContext, MockDispatcher mockDispatcher) {
		this.servletContext = servletContext;
		this.mockDispatcher = mockDispatcher;
	}

    // Factory methods

    public static MockMvc createFromApplicationContext(ApplicationContext applicationContext) {
        // TODO
        return null;
    }

    public static MockMvc createFromWebXml(String webXmlFileName) {
        // TODO
        return null;
    }

    // Perform

    public MvcResultActions perform(MockHttpServletRequestBuilder requestBuilder) {
        Assert.notNull(requestBuilder, "'requestBuilder' must not be null");
        // TODO
        return null;
    }

}
