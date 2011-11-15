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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.JsonPathExpectationsHelper;


/**
 * TODO ...
 * 
 * @author Rossen Stoyanchev
 */
public class JsonPathResultMatchers {

	private JsonPathExpectationsHelper jsonPathHelper;
	
	/**
	 * TODO
	 */
	public JsonPathResultMatchers(String expression, Object ... args) {
		this.jsonPathHelper = new JsonPathExpectationsHelper(expression, args);
	}

	/**
	 * TODO
	 */
	public <T> ResultMatcher value(final Matcher<T> matcher) {
		return new ResultMatcherAdapter() {

			@Override
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				jsonPathHelper.assertValue(response.getContentAsString(), matcher);
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher value(Object value) {
		return value(Matchers.equalTo(value));
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher exists() {
		return new ResultMatcherAdapter() {
			
			@Override
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				jsonPathHelper.exists(response.getContentAsString());
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher doesNotExist() {
		return new ResultMatcherAdapter() {
			
			@Override
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				jsonPathHelper.doesNotExist(response.getContentAsString());
			}
		};
	}

}
