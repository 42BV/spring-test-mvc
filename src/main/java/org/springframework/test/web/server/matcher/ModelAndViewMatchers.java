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
import static org.springframework.test.web.AssertionErrors.fail;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MvcResultMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Matchers for assertions on a {@link ModelAndView}.  
 *
 */
public abstract class ModelAndViewMatchers {

	private ModelAndViewMatchers() {
	}

	public static MvcResultMatcher modelAttribute(final String name, final Object value) {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				assertEquals("Model attribute", value, mav.getModel().get(name));
			}
		};
	}

	public static MvcResultMatcher modelAttributesPresent(final String...names) {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				AssertionErrors.assertNameValuesPresent("Model attribute", mav.getModelMap(), names);
			}
		};
	}

	public static MvcResultMatcher modelAttributesNotPresent(final String...names) {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				AssertionErrors.assertNameValuesNotPresent("Model attribute", mav.getModelMap(), names);
			}
		};
	}

	public static MvcResultMatcher noBindingErrors() {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				for (String name : mav.getModel().keySet()) {
					if (!name.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
						continue;
					}
					BindingResult result = (BindingResult) mav.getModel().get(name);
					if (result.hasErrors()) {
						fail("Model attribute <" + name + "> has binding errors: " + result);
					}
				}
			}
		};
	}

	public static MvcResultMatcher modelAttributesWithNoErrors(final String...names) {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				AssertionErrors.assertNameValuesPresent("Model attribute", mav.getModelMap(), names);
				for (String name : names) {
					BindingResult result = (BindingResult) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
					if (result.hasErrors()) {
						fail("Expected no bind errors for model attribute <" + name + "> but got " + result);
					}
				}
			}
		};
	}
	
	public static MvcResultMatcher modelAttributesWithErrors(final String...names) {
		return new ModelAndViewResultMatcher() {
			protected void matchModelAndView(ModelAndView mav) {
				AssertionErrors.assertNameValuesPresent("Model attribute", mav.getModelMap(), names);
				for (String name : names) {
					BindingResult result = (BindingResult) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
					assertTrue("Expected bind errors for model attribute <" + name + ">", result.hasErrors());
				}
			}
		};
	}

	public static MvcResultMatcher viewName(final String viewName) {
		return new MvcResultMatcher() {
			public void match(MockHttpServletRequest request, 
							  MockHttpServletResponse response, 
							  Object handler, 
							  ModelAndView mav) {
				assertEquals("View name", viewName, mav.getViewName());
			}
		};
	}

	private abstract static class ModelAndViewResultMatcher implements MvcResultMatcher {

		public final void match(MockHttpServletRequest request, 
								MockHttpServletResponse response, 
								Object handler, 
								ModelAndView mav) {
			assertTrue("No ModelAndView", mav != null);
			matchModelAndView(mav);
		}

		protected abstract void matchModelAndView(ModelAndView mav);
	}
	
}
