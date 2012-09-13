/*
 * Copyright 2011 the original author or authors.
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
import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.lang.reflect.Method;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

public class HandlerResultMatchers {

	/**
	 * TODO
	 */
	public ResultMatcher handlerType(final Class<?> type) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Object handler = result.getHandler();
				assertTrue("No handler: ", handler != null);
				Class<?> actual = handler.getClass();
				if (HandlerMethod.class.isInstance(handler)) {
					actual = ((HandlerMethod) handler).getBeanType();
				}
				assertEquals("Handler type", type, ClassUtils.getUserClass(actual));
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher methodName(final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Object handler = result.getHandler();
				assertTrue("No handler: ", handler != null);
				assertTrue("Not a HandlerMethod: " + handler, HandlerMethod.class.isInstance(handler));
				MatcherAssert.assertThat("HandlerMethod", ((HandlerMethod) handler).getMethod().getName(), matcher);
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher methodName(final String name) {
		return methodName(Matchers.equalTo(name));
	}

	/**
	 * TODO
	 */
	public ResultMatcher method(final Method method) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Object handler = result.getHandler();
				assertTrue("No handler: ", handler != null);
				assertTrue("Not a HandlerMethod: " + handler, HandlerMethod.class.isInstance(handler));
				assertEquals("HandlerMethod", method, ((HandlerMethod) handler).getMethod());
			}
		};
	}
	
}
