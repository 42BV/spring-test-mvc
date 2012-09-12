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

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

public class RequestResultMatchers {

	/**
	 * TODO
	 */
	public <T> ResultMatcher attribute(final String name, final Matcher<T> matcher) {
		return new ResultMatcher() {
			@SuppressWarnings("unchecked")
			public void match(MvcResult result) {
				T value = (T) result.getRequest().getAttribute(name);
				MatcherAssert.assertThat("Request attribute: ", value, matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public <T> ResultMatcher attribute(String name, Object value) {
		return attribute(name, Matchers.equalTo(value));
	}
	
	/**
	 * TODO
	 */
	public <T> ResultMatcher sessionAttribute(final String name, final Matcher<T> matcher) {
		return new ResultMatcher() {
			@SuppressWarnings("unchecked")
			public void match(MvcResult result) {
				T value = (T) result.getRequest().getSession().getAttribute(name);
				MatcherAssert.assertThat("Request attribute: ", value, matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public <T> ResultMatcher sessionAttribute(String name, Object value) {
		return sessionAttribute(name, Matchers.equalTo(value));
	}

}
