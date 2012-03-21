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
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.JsonPathExpectationsHelper;

import java.util.List;

import static org.hamcrest.Matchers.*;


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
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				JsonPathResultMatchers.this.jsonPathHelper.assertValue(content, matcher);
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher value(Object value) {
		return value(equalTo(value));
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher exists() {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				JsonPathResultMatchers.this.jsonPathHelper.exists(content);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher doesNotExist() {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				JsonPathResultMatchers.this.jsonPathHelper.doesNotExist(content);
			}
		};
	}

	/**
	 * Assert a json path is an array
	 */
	public ResultMatcher isArray() {
		return value(isA(List.class));
	}
}
