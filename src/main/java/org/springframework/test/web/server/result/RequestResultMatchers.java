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

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MockMvcResultMatcher;
import org.springframework.test.web.server.MockMvcResult;

/**
 * Request-related matchers.
 *
 * @author Rossen Stoyanchev
 */
public class RequestResultMatchers {

	RequestResultMatchers() {
	}

	public MockMvcResultMatcher requestAttributeValue(final String name, final Object value) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				assertEquals("Request attribute", value, result.getRequest().getAttribute(name));
			}
		};
	}

	public MockMvcResultMatcher requestAttributesPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				Map<String, Object> attrs = getRequestAttributeMap(result.getRequest());
				AssertionErrors.assertNameValuesPresent("Request attribute", attrs, names);
			}
		};
	}
	
	public MockMvcResultMatcher requestAttributesNotPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				Map<String, Object> attrs = getRequestAttributeMap(result.getRequest());
				AssertionErrors.assertNameValuesNotPresent("Request attribute", attrs, names);
			}
		};
	}

	public MockMvcResultMatcher sessionAttributeValue(final String name, final Object value) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				assertEquals("Session attribute", value, result.getRequest().getSession().getAttribute(name));
			}
		};
	}

	public MockMvcResultMatcher sessionAttributesPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				HttpSession session = result.getRequest().getSession();
				AssertionErrors.assertNameValuesPresent("Session attribute", getSessionAttributeMap(session), names);
			}
		};
	}
	
	public MockMvcResultMatcher sessionAttributesNotPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				HttpSession session = result.getRequest().getSession();
				AssertionErrors.assertNameValuesNotPresent("Session attribute", getSessionAttributeMap(session), names);
			}
		};
	}

	static Map<String, Object> getHeaderValueMap(MockHttpServletRequest request) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<?> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, request.getHeader(name));
		}
		return map;
	}

	static Map<String, Object> getRequestAttributeMap(ServletRequest request) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<?> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, request.getAttribute(name));
		}
		return map;
	}

	static Map<String, Object> getSessionAttributeMap(HttpSession session) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<?> names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, session.getAttribute(name));
		}

		return map;
	}

}
