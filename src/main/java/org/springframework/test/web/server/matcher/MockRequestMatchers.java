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

package org.springframework.test.web.server.matcher;

import static org.springframework.test.web.AssertionErrors.assertEquals;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MvcResultMatcher;
import org.springframework.web.servlet.ModelAndView;

/**
 * Matchers assertions on a {@link MockHttpServletRequest}.
 *
 */
public abstract class MockRequestMatchers {

	private MockRequestMatchers() {
	}

	public static MvcResultMatcher requestAttributeValue(final String name, final Object value) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				assertEquals("Request attribute", value, request.getAttribute(name));
			}
		};
	}

	public static MvcResultMatcher requestAttributesPresent(final String...names) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				AssertionErrors.assertNameValuesPresent("Request attribute", getRequestAttributeMap(request), names);
			}
		};
	}
	
	public static MvcResultMatcher requestAttributesNotPresent(final String...names) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				AssertionErrors.assertNameValuesNotPresent("Request attribute", getRequestAttributeMap(request), names);
			}
		};
	}

	public static MvcResultMatcher sessionAttributeValue(final String name, final Object value) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				assertEquals("Session attribute", value, request.getSession().getAttribute(name));
			}
		};
	}

	public static MvcResultMatcher sessionAttributesPresent(final String...names) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				HttpSession session = request.getSession();
				AssertionErrors.assertNameValuesPresent("Session attribute", getSessionAttributeMap(session), names);
			}
		};
	}
	
	public static MvcResultMatcher sessionAttributesNotPresent(final String...names) {
		return new MockRequestResultMatcher() {
			protected void matchMockRequest(MockHttpServletRequest request) {
				HttpSession session = request.getSession();
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

	private abstract static class MockRequestResultMatcher implements MvcResultMatcher {

		public final void match(MockHttpServletRequest request, 
								MockHttpServletResponse response, 
								Object handler, 
								Exception handlerException,
								ModelAndView mav) {
			matchMockRequest(request);
		}

		protected abstract void matchMockRequest(MockHttpServletRequest request);
	}

}
