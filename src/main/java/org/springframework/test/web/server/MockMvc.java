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

package org.springframework.test.web.server;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.Assert;

/**
 * <strong>Main entry point for server-side Spring MVC test support.</strong>
 *
 * <p>Example, assuming static imports of {@code MockMvcBuilders.*},
 * {@code MockMvcRequestBuilders.*} and {@code MockMvcResultMatchers.*}:
 *
 * <pre>
 * MockMvc mockMvc =
 *     annotationConfigMvcSetup(TestConfiguration.class)
 *         .configureWarRootDir("src/main/webapp", false).build()
 *
 * mockMvc.perform(get("/form"))
 *     .andExpect(status().isOk())
 *     .andExpect(content().mimeType("text/plain"))
 *     .andExpect(forwardedUrl("/WEB-INF/layouts/main.jsp"));
 * </pre>
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 */
public class MockMvc {

	static String MVC_RESULT_ATTRIBUTE = MockMvc.class.getName().concat(".MVC_RESULT_ATTRIBUTE");

	private final MockFilterChain filterChain;

	private final ServletContext servletContext;

	/**
	 * Protected constructor not for direct instantiation.
	 * @see org.springframework.test.web.server.setup.MockMvcBuilders
	 */
	protected MockMvc(MockFilterChain filterChain, ServletContext servletContext) {
		Assert.notNull(servletContext, "A ServletContext is required");
		Assert.notNull(filterChain, "A MockFilterChain is required");

		this.filterChain = filterChain;
		this.servletContext = servletContext;
	}

	/**
	 * Execute a request and return a {@link ResultActions} instance that wraps
	 * the results and enables further actions such as setting up expectations.
	 *
	 * @param requestBuilder used to prepare the request to execute;
	 * see static factory methods in
	 * {@link org.springframework.test.web.server.request.MockMvcRequestBuilders}
	 * @return A ResultActions instance; never {@code null}
	 * @throws Exception any exception not handled by a HandlerExceptionResolver occurs
	 * @see org.springframework.test.web.server.request.MockMvcRequestBuilders
	 * @see org.springframework.test.web.server.result.MockMvcResultMatchers
	 */
	public ResultActions perform(RequestBuilder requestBuilder) throws IOException, ServletException {

		MockHttpServletRequest request = requestBuilder.buildRequest(this.servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse();

		final MvcResult mvcResult = new DefaultMvcResult(request, response);
		request.setAttribute(MVC_RESULT_ATTRIBUTE, mvcResult);

		this.filterChain.reset();
		this.filterChain.doFilter(request, response);

		return new ResultActions() {

			public ResultActions andExpect(ResultMatcher matcher) throws Exception {
				matcher.match(mvcResult);
				return this;
			}

			public ResultActions andDo(ResultHandler printer) throws Exception {
				printer.handle(mvcResult);
				return this;
			}

			public MvcResult andReturn() {
				return mvcResult;
			}
		};
	}

}
