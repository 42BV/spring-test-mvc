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

import javax.servlet.http.Cookie;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

public class CookieResultMatchers {

	/**
	 * Assert a cookie value with a {@link Matcher}.
	 */
	public ResultMatcher value(final String name, final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				Cookie cookie = result.getResponse().getCookie(name);
				assertTrue("Response cookie not found: " + name, cookie != null);
				MatcherAssert.assertThat("Response cookie", cookie.getValue(), matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher value(final String name, final String value) {
		return value(name, Matchers.equalTo(value));
	}

}
