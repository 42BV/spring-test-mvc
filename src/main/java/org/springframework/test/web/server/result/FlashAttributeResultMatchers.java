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

import static org.springframework.test.web.AssertionErrors.*;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

public class FlashAttributeResultMatchers {

	/**
	 * TODO
	 */
	public <T> ResultMatcher attribute(final String name, final Matcher<T> matcher) {
		return new ResultMatcher() {
			@SuppressWarnings("unchecked")
			public void match(MvcResult result) throws Exception {
				MatcherAssert.assertThat("Flash attribute", (T) result.getFlashMap().get(name), matcher);
			}
		};
	}
	
	/**
	 * Syntactic sugar, equivalent to:
	 * <pre>
	 * flashAttribute("attrName", equalTo("attrValue"))
	 * </pre>
	 */
	public <T> ResultMatcher attribute(final String name, final Object value) {
		return attribute(name, Matchers.equalTo(value));
	}
	
	/**
	 * Syntactic sugar, equivalent to:
	 * <pre>
	 * flashAttribute("attrName", notNullValue())
	 * </pre>
	 */
	public <T> ResultMatcher attributeExists(final String... names) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				for (String name : names) {
					attribute(name, Matchers.notNullValue()).match(result);
				}
			}
		};
	}	

	/**
	 * TODO
	 */
	public <T> ResultMatcher attributeCount(final int count) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				assertEquals("FlashMap size", count, result.getFlashMap().size());
			}
		};
	}	
	
}
