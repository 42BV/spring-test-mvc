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

import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MockMvcResultMatcher;
import org.springframework.test.web.server.MockMvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Model-related matchers.
 * 
 * @author Rossen Stoyanchev
 */
public class ModelResultMatchers {

	ModelResultMatchers() {
	}

	public MockMvcResultMatcher modelAttribute(final String name, final Object value) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				assertEquals("Model attribute", value, getModel(result).get(name));
			}

		};
	}

	public MockMvcResultMatcher modelAttributesPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				AssertionErrors.assertNameValuesPresent("Model attribute", getModel(result), names);
			}
		};
	}

	public MockMvcResultMatcher modelAttributesNotPresent(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				AssertionErrors.assertNameValuesNotPresent("Model attribute", getModel(result), names);
			}
		};
	}

	public MockMvcResultMatcher noBindingErrors() {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				Map<String, Object> model = getModel(result);
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

	public MockMvcResultMatcher modelAttributesWithNoErrors(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				Map<String, Object> model = getModel(result);
				AssertionErrors.assertNameValuesPresent("Model attribute", model, names);
				for (String name : names) {
					BindingResult bindingResult = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + name);
					if (bindingResult.hasErrors()) {
						fail("Expected no bind errors for model attribute <" + name + "> but got " + result);
					}
				}
			}
		};
	}
	
	public MockMvcResultMatcher modelAttributesWithErrors(final String...names) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				Map<String, Object> model = getModel(result);
				AssertionErrors.assertNameValuesPresent("Model attribute", model, names);
				for (String name : names) {
					BindingResult bindingResult = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + name);
					assertTrue("Expected bind errors for model attribute <" + name + ">", bindingResult.hasErrors());
				}
			}
		};
	}

	private Map<String, Object> getModel(MockMvcResult result) {
		ModelAndView mav = result.getModelAndView();
		assertTrue("No ModelAndView", mav != null);
		Map<String, Object> model = mav.getModel();
		return model;
	}

}
