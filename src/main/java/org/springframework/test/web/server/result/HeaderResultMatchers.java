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

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

public class HeaderResultMatchers {

	/**
	 * Assert a response header with the given {@link Matcher}.
	 */
	public ResultMatcher string(final String name, final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				MatcherAssert.assertThat("Response header", result.getResponse().getHeader(name), matcher);
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher string(final String name, final String value) {
		return string(name, Matchers.equalTo(value));
	}

	/**
	 * TODO
	 */
	public ResultMatcher longValue(final String name, final long value) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertEquals("Response header " + name, value, Long.parseLong(result.getResponse().getHeader(name)));
			}
		};
	}

}
