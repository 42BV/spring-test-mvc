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
import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.lang.reflect.Method;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
		return new HandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				assertEquals("Handler method", methodName, handlerMethod.getMethod().getName());
			}
		};
	}
	
	public ResultMatcher method(final Class<?> controllerType, 
								final String methodName, 
								final Class<?>...argumentTypes) {
		return new HandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				Method method = ReflectionUtils.findMethod(controllerType, methodName, argumentTypes);
				assertTrue("Handler method not found", method != null);
				assertEquals("Method", method, handlerMethod.getMethod());
			}
		};
	}

	public ResultMatcher type(final Class<?> handlerType) {
		return new HandlerResultMatcher() {
			protected void matchHandler(Object handler) {
				assertEquals("Handler type", handlerType, handler.getClass());
			}
		};
	}

	/**
	 * Base class for Matchers that assert the matched handler.
	 */
	public abstract static class HandlerResultMatcher implements ResultMatcher {

		public final void match(MockHttpServletRequest request, 
				MockHttpServletResponse response, 
				Object handler,	
				HandlerInterceptor[] interceptors, 
				ModelAndView mav, 
				Exception resolvedException) throws Exception {
			
			assertTrue("No matching handler", handler != null);
			matchHandler(handler);
		}

		protected abstract void matchHandler(Object handler) throws Exception;
	}

	/**
	 * Base class for Matchers that assert a matched handler of type {@link HandlerMethod}.
	 */
	private abstract static class HandlerMethodResultMatcher extends HandlerResultMatcher {

		@Override
		protected void matchHandler(Object controller) throws Exception {
			Class<?> type = controller.getClass();
			assertTrue("Not a HandlerMethod. Actual type " + type, HandlerMethod.class.isAssignableFrom(type));
			matchHandlerMethod((HandlerMethod) controller);
		}

		protected abstract void matchHandlerMethod(HandlerMethod handlerMethod) throws Exception;
	}
	
}
