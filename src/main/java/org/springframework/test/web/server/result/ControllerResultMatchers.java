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

import org.springframework.test.web.server.MockMvcResultMatcher;
import org.springframework.test.web.server.MockMvcResult;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * Controller-related matchers.
 * 
 * @author Rossen Stoyanchev
 */
public class ControllerResultMatchers {

	ControllerResultMatchers() {
	}

	public MockMvcResultMatcher methodName(final String methodName) {
		return new ControllerMethodResultMatcher() {
			protected void matchMethod(HandlerMethod controllerMethod) {
				assertEquals("Controller method", methodName, controllerMethod.getMethod().getName());
			}
		};
	}
	
	public MockMvcResultMatcher method(final Class<?> controllerType, 
								   final String methodName, 
								   final Class<?>...argumentTypes) {
		return new ControllerMethodResultMatcher() {
			protected void matchMethod(HandlerMethod handlerMethod) {
				Method method = ReflectionUtils.findMethod(controllerType, methodName, argumentTypes);
				assertTrue("Controller method not found", method != null);
				assertEquals("Method", method, handlerMethod.getMethod());
			}
		};
	}

	public MockMvcResultMatcher controllerType(final Class<?> controllerType) {
		return new ControllerResultMatcher() {
			protected void matchInternal(Object handler) {
				assertEquals("Controller type", controllerType, handler.getClass());
			}
		};
	}

	private abstract static class ControllerResultMatcher implements MockMvcResultMatcher {

		public final void match(MockMvcResult result) {
			assertTrue("No matching controller", result.getController() != null);
			matchInternal(result.getController());
		}

		protected abstract void matchInternal(Object controller);
	}

	private abstract static class ControllerMethodResultMatcher extends ControllerResultMatcher {

		@Override
		protected void matchInternal(Object controller) {
			Class<?> type = controller.getClass();
			assertTrue("Not a HandlerMethod. Actual type " + type, HandlerMethod.class.isAssignableFrom(type));
			matchMethod((HandlerMethod) controller);
		}

		protected abstract void matchMethod(HandlerMethod handlerMethod);
	}
	
}
