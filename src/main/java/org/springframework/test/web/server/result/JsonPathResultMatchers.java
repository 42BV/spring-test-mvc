/*
 * Copyright 2011 the original author or authors.
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;

import java.util.List;

import org.hamcrest.Matcher;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.JsonPathExpectationsHelper;


/**
 * Factory for response content {@code ResultMatcher}'s using a <a
 * href="http://goessner.net/articles/JsonPath/">JSONPath</a> expression. An
 * instance of this class is typically accessed via
 * {@code MockMvcResultMatchers.jsonPpath(..)}.
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
	 * Evaluate the JSONPath and assert the resulting value with the given {@code Matcher}.
	 */
	public <T> ResultMatcher value(final Matcher<T> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				jsonPathHelper.assertValue(content, matcher);
			}
		};
	}

	/**
	 * Apply the JSONPath and assert the resulting value.
	 */
	public ResultMatcher value(Object value) {
		return value(equalTo(value));
	}

	/**
	 * Apply the JSONPath and assert the resulting value.
	 */
	public ResultMatcher exists() {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				jsonPathHelper.exists(content);
			}
		};
	}

	/**
	 * Evaluate the JSON path and assert the resulting content exists.
	 */
	public ResultMatcher doesNotExist() {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				jsonPathHelper.doesNotExist(content);
			}
		};
	}

	/**
	 * Assert the content at the given JSONPath is an array.
	 */
	public ResultMatcher isArray() {
		return value(isA(List.class));
	}
}
