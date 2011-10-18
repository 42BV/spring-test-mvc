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

import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.validation.BindingResult;

/**
 * Provides methods to define expectations on model attributes.
 * 
 * @author Rossen Stoyanchev
 */
public class ModelResultMatchers {

	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#model()
	 */
	protected ModelResultMatchers() {
	}

	public ResultMatcher attribute(final String attributeName, final Object attributeValue) {
		return attribute(attributeName, Matchers.equalTo(attributeValue));
	}

	public ResultMatcher attribute(final String name, final Matcher<Object> matcher) {
		return new AbstractModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				MatcherAssert.assertThat("Model attribute", model.get(name), matcher);
			}
		};
	}

	/**
	 * Assert the actual number of attributes in the model excluding 
	 * BindingResult attributes.
	 */
	public ResultMatcher size(final int expectedSize) {
		return new AbstractModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				int actualSize = 0;
				for (String key : model.keySet()) {
					if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
						actualSize++;
					}
				}
				AssertionErrors.assertEquals("Model size", expectedSize, actualSize);
			}
		};
	}

	public ResultMatcher hasErrorsForAttribute(final String attributeName) {
		return new AbstractModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertTrue("Attribute not found: " + attributeName, model.get(attributeName) != null);
				BindingResult result = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + attributeName);
				AssertionErrors.assertTrue("BindingResult not found: " + attributeName, result != null);
				AssertionErrors.assertTrue("Expected errors for attribute: " + attributeName, result.hasErrors());
			}
		};
	}

	public ResultMatcher hasAttributes(final String...attributeNames) {
		return new AbstractModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				for (String name : attributeNames) {
					if (!model.containsKey(name)) {
						AssertionErrors.fail("Model attribute <" + name + "> not found.");
					}
				}
			}
		};
	}
	
	public ResultMatcher hasNoErrors() {
		return new AbstractModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				for (Object value : model.values()) {
					if (value instanceof BindingResult) {
						BindingResult result = (BindingResult) value;
						AssertionErrors.assertTrue("Unexpected binding error(s): " + result, !result.hasErrors());
					}
				}
			}
		};
	}

}
