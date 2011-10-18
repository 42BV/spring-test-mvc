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

import java.lang.reflect.Method;

import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * Provides methods to define expectations on the selected handler.
 * 
 * @author Rossen Stoyanchev
 */
public class HandlerResultMatchers {

	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#handler()  
	 */
	HandlerResultMatchers() {
	}

	public ResultMatcher methodName(final String methodName) {
		return new AbstractHandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				AssertionErrors.assertEquals("Handler method", methodName, handlerMethod.getMethod().getName());
			}
		};
	}
	
	public ResultMatcher method(final Class<?> controllerType, 
								final String methodName, 
								final Class<?>...argumentTypes) {
		return new AbstractHandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				Method method = ReflectionUtils.findMethod(controllerType, methodName, argumentTypes);
				AssertionErrors.assertTrue("Handler method not found", method != null);
				AssertionErrors.assertEquals("Method", method, handlerMethod.getMethod());
			}
		};
	}

	public ResultMatcher type(final Class<?> handlerType) {
		return new AbstractHandlerResultMatcher() {
			protected void matchHandler(Object handler) {
				AssertionErrors.assertEquals("Handler type", handlerType, handler.getClass());
			}
		};
	}

	/**
	 * Base class for assertions on a handler of type {@link HandlerMethod}.
	 */
	private abstract static class AbstractHandlerMethodResultMatcher extends AbstractHandlerResultMatcher {

		public final void matchHandler(Object handler) throws Exception {
			Class<?> type = handler.getClass();
			boolean result = HandlerMethod.class.isAssignableFrom(type);
			AssertionErrors.assertTrue("Not a HandlerMethod. Actual type " + type, result);
			matchHandlerMethod((HandlerMethod) handler);
		}
		
		protected abstract void matchHandlerMethod(HandlerMethod handlerMethod) throws Exception;
	}

}
