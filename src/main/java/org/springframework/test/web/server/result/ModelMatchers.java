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
import static org.springframework.test.web.AssertionErrors.fail;

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Matchers with expectations on the resulting model.
 * 
 * @author Rossen Stoyanchev
 */
public class ModelMatchers {

	ModelMatchers() {
	}

	public ResultMatcher modelAttribute(final String name, final Object value) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				assertEquals("Model attribute", value, model.get(name));
			}

		};
	}

	public ResultMatcher modelAttributesPresent(final String...names) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertNameValuesPresent("Model attribute", model, names);
			}
		};
	}

	public ResultMatcher modelAttributesNotPresent(final String...names) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertNameValuesNotPresent("Model attribute", model, names);
			}
		};
	}

	public ResultMatcher noBindingErrors() {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				for (String name : model.keySet()) {
					if (!name.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
						continue;
					}
					BindingResult bindingResult = (BindingResult) model.get(name);
					if (bindingResult.hasErrors()) {
						fail("Model attribute <" + name + "> has binding errors: " + bindingResult);
					}
				}
			}
		};
	}

	public ResultMatcher modelAttributesWithNoErrors(final String...names) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertNameValuesPresent("Model attribute", model, names);
				for (String name : names) {
					BindingResult bindingResult = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + name);
					if (bindingResult.hasErrors()) {
						fail("Expected no bind errors for model attribute <" + name + "> but got " + bindingResult);
					}
				}
			}
		};
	}
	
	public ResultMatcher modelAttributesWithErrors(final String...names) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertNameValuesPresent("Model attribute", model, names);
				for (String name : names) {
					BindingResult bindingResult = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + name);
					assertTrue("Expected bind errors for model attribute <" + name + ">", bindingResult.hasErrors());
				}
				
			}
		};
	}

	public static abstract class ModelResultMatcher implements ResultMatcher {

		public final void match(MockHttpServletRequest request, 
								MockHttpServletResponse response, 
								Object handler,	
								HandlerInterceptor[] interceptors, 
								ModelAndView mav, 
								Exception resolvedException) {
			
			assertTrue("No ModelAndView", mav != null);
			matchModel(mav.getModel());
		}

		protected abstract void matchModel(Map<String, Object> model);
	}

}
