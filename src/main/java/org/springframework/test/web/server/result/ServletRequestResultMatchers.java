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

package org.springframework.test.web.server.result;

import static org.springframework.test.web.AssertionErrors.assertEquals;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.server.ResultMatcher;

/**
 * Provides methods to define expectations on the HttpServletRequest.
 *
 * @author Rossen Stoyanchev
 */
public class ServletRequestResultMatchers {

	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#request()
	 */
	protected ServletRequestResultMatchers() {
	}

	/**
	 * Obtain a request attribute and match it to the {@code expectedValue}.
	 */
	public ResultMatcher requestAttribute(final String name, final Object expectedValue) {
		return new AbstractServletRequestResultMatcher() {
			public void matchRequest(MockHttpServletRequest request) {
				assertEquals("Request attribute", expectedValue, request.getAttribute(name));
			}
		};
	}

	/**
	 * Obtain a session attribute and match it to the {@code expectedValue}.
	 */
	public ResultMatcher sessionAttribute(final String name, final Object expectedValue) {
		return new AbstractServletRequestResultMatcher() {
			public void matchRequest(MockHttpServletRequest request) {
				assertEquals("Session attribute", expectedValue, request.getSession().getAttribute(name));
			}
		};
	}
	
}
