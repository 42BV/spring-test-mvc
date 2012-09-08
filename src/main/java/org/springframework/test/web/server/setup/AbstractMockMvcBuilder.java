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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.NestedRuntimeException;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.TestDispatcherServlet;
import org.springframework.web.context.WebApplicationContext;

/**
 * An abstract class for building {@link MockMvc} instances.
 *
 * @author Rossen Stoyanchev
 */
public abstract class AbstractMockMvcBuilder implements MockMvcBuilder {

	/**
	 * Build a {@link MockMvc} instance.
	 */
	public final MockMvc build() {

		ServletContext servletContext = initServletContext();
		WebApplicationContext wac = initWebApplicationContext(servletContext);

		ServletConfig config = new MockServletConfig(servletContext);
		TestDispatcherServlet dispatcherServlet = new TestDispatcherServlet(wac);
		try {
			dispatcherServlet.init(config);
		}
		catch (ServletException ex) {
			// should never happen..
			throw new MockMvcBuildException("Failed to init DispatcherServlet", ex);
		}

		return new MockMvc(dispatcherServlet) {};
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


	@SuppressWarnings("serial")
	private static class MockMvcBuildException extends NestedRuntimeException {

		public MockMvcBuildException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}
}
