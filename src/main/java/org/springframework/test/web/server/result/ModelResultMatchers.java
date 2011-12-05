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

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class ModelResultMatchers {

	/**
	 * TODO
	 */
	public <T> ResultMatcher attribute(final String name, final Matcher<T> matcher) {
		return new ResultMatcher() {
			@SuppressWarnings("unchecked")
			public void match(MvcResult result) throws Exception {
				assertTrue("No ModelAndView found", result.getModelAndView() != null);
				MatcherAssert.assertThat("Model attribute", (T) result.getModelAndView().getModel().get(name), matcher);
			}
		};
	}
	
	/**
	 * Syntactic sugar, equivalent to:
	 * <pre>
	 * modelAttribute("attrName", equalTo("attrValue"))
	 * </pre>
	 */
	public ResultMatcher attribute(String name, Object value) {
		return attribute(name, Matchers.equalTo(value));
	}
	
	/**
	 * Syntactic sugar, equivalent to:
	 * <pre>
	 * modelAttribute("attrName", notNullValue())
	 * </pre>
	 */
	public ResultMatcher attributeExists(final String... names) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				assertTrue("No ModelAndView found", result.getModelAndView() != null);
				for (String name : names) {
					attribute(name, Matchers.notNullValue());
				}
			}
		};
	}

	/**
	 * TODO
	 */
	public <T> ResultMatcher attributeHasErrors(final String... names) {
		return new ResultMatcher() {
			public void match(MvcResult mvcResult) throws Exception {
				ModelAndView mav = mvcResult.getModelAndView();
				assertTrue("No ModelAndView found", mav != null);
				for (String name : names) {
					BindingResult result = (BindingResult) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
					assertTrue("No BindingResult for attribute: " + name, result != null);
					assertTrue("No errors for attribute: " + name, result.hasErrors());
				}
			}
		};
	}

	/**
	 * TODO
	 */
	public <T> ResultMatcher hasNoErrors() {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				assertTrue("No ModelAndView found", result.getModelAndView() != null);
				for (Object value : result.getModelAndView().getModel().values()) {
					if (value instanceof BindingResult) {
						assertTrue("Unexpected binding error(s): " + value, !((BindingResult) value).hasErrors());
					}
				}
			}
		};
	}
	
	/**
	 * Assert the number of attributes excluding BindingResult instances.
	 */
	public <T> ResultMatcher size(final int size) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				AssertionErrors.assertTrue("No ModelAndView found", result.getModelAndView() != null);
				int actual = 0;
				for (String key : result.getModelAndView().getModel().keySet()) {
					if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
						actual++;
					}
				}
				assertEquals("Model size", size, actual);
			}
		};
	}

}
