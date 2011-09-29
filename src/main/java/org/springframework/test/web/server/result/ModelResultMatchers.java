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

import static org.springframework.test.web.AssertionErrors.assertTrue;
import static org.springframework.test.web.AssertionErrors.fail;

import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				MatcherAssert.assertThat("Model attribute", model.get(name), matcher);
			}
		};
	}

	public ResultMatcher hasErrorsForAttribute(final String attributeName) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				AssertionErrors.assertTrue("Attribute not found: " + attributeName, model.get(attributeName) != null);
				BindingResult result = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + attributeName);
				AssertionErrors.assertTrue("BindingResult not found: " + attributeName, result != null);
				assertTrue("Expected errors for attribute: " + attributeName, result.hasErrors());
			}
		};
	}

	public ResultMatcher hasAttributes(final String...attributeNames) {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				for (String name : attributeNames) {
					if (!model.containsKey(name)) {
						fail("Model attribute <" + name + "> not found.");
					}
				}
			}
		};
	}
	
	public ResultMatcher hasNoErrors() {
		return new ModelResultMatcher() {
			public void matchModel(Map<String, Object> model) {
				for (Object value : model.values()) {
					if (value instanceof BindingResult) {
						BindingResult result = (BindingResult) value;
						assertTrue("Unexpected binding error(s): " + result, !result.hasErrors());
					}
				}
			}
		};
	}

	/**
	 * Base class for Matchers that assert model attributes.
	 */
	public static abstract class ModelResultMatcher implements ResultMatcher {

		public final void match(MockHttpServletRequest request, 
								MockHttpServletResponse response, 
								Object handler,	
								HandlerInterceptor[] interceptors, 
								ModelAndView mav, 
								Exception resolvedException) throws Exception {
			
			assertTrue("No ModelAndView", mav != null);
			matchModel(mav.getModel());
		}

		protected abstract void matchModel(Map<String, Object> model) throws Exception;
	}

}
