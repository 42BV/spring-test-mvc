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
import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.lang.reflect.Method;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MvcResultMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Matchers for applying assertions on the handler matched to a request.
 * 
 */
public abstract class HandlerMatchers {

	private HandlerMatchers() {
	}

	public static MvcResultMatcher handlerMethod(final String methodName) {
		return new HandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				assertEquals("Method", methodName, handlerMethod.getMethod().getName());
			}
		};
	}
	
	public static MvcResultMatcher handlerMethod(final Class<?> controllerType, 
											  final String methodName, 
											  final Class<?>...argumentTypes) {
		return new HandlerMethodResultMatcher() {
			protected void matchHandlerMethod(HandlerMethod handlerMethod) {
				Method method = ReflectionUtils.findMethod(controllerType, methodName, argumentTypes);
				assertTrue("Method not found", method != null);
				assertEquals("Method", method, handlerMethod.getMethod());
			}
		};
	}

	public static MvcResultMatcher handlerType(final Class<?> handlerType) {
		return new HandlerResultMatcher() {
			protected void matchHandler(Object handler) {
				assertEquals("Handler type", handlerType, handler.getClass());
			}
		};
	}

	private abstract static class HandlerResultMatcher implements MvcResultMatcher {

		public final void match(MockHttpServletRequest request, 
								MockHttpServletResponse response, 
								Object handler, 
								ModelAndView mav) {
			assertTrue("No matching handler", handler != null);
			matchHandler(handler);
		}

		protected abstract void matchHandler(Object handler);
	}

	private abstract static class HandlerMethodResultMatcher extends HandlerResultMatcher {

		@Override
		protected void matchHandler(Object handler) {
			Class<?> type = handler.getClass();
			assertTrue("Expected HandlerMethod. Actual type " + type, HandlerMethod.class.isAssignableFrom(type));
			matchHandlerMethod((HandlerMethod) handler);
		}

		protected abstract void matchHandlerMethod(HandlerMethod handlerMethod);
	}
	
}
