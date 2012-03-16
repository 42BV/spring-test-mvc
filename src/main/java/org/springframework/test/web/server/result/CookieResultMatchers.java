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
	public ResultMatcher value(String name, String value) {
		return value(name, Matchers.equalTo(value));
	}

	/**
	 * Assert a cookie exists and its max age is not equals to 0 (expired cookie)
	 */
	public ResultMatcher exists(final String name) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie not found: " + name,
										 cookie != null && cookie.getMaxAge() != 0);
			}
		};
	}

	/**
	 * Assert a cookie doesn't exist or its maxAge is equals to 0 (expired cookie)
	 */
	public ResultMatcher doesNotExist(final String name) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Expected no response cookie but found one with name " + name,
										 cookie == null || cookie.getMaxAge() == 0);
			}
		};
	}

	/**
	 * Assert a cookie max age with a {@link Matcher}
	 */
	public ResultMatcher maxAge(final String name, final Matcher<? super Integer> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result)
					throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie maxAge", cookie.getMaxAge(), matcher);
			}
		};
	}

	public ResultMatcher maxAge(String name, int maxAge) {
		return maxAge(name, Matchers.equalTo(maxAge));
	}

	/**
	 * Assert a cookie path with a {@link Matcher}
	 */
	public ResultMatcher path(final String name, final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie path", cookie.getPath(), matcher);
			}
		};
	}

	public ResultMatcher path(String name, String path) {
		return path(name, Matchers.equalTo(path));
	}

	/**
	 * Assert a cookie domain with a {@link Matcher}
	 */
	public ResultMatcher domain(final String name, final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie domain", cookie.getDomain(), matcher);
			}
		};
	}

	public ResultMatcher domain(String name, String domain) {
		return domain(name, Matchers.equalTo(domain));
	}

	/**
	 * Assert a cookie comment with a {@link Matcher}
	 */
	public ResultMatcher comment(final String name, final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie comment", cookie.getComment(), matcher);
			}
		};
	}

	public ResultMatcher comment(String name, String comment) {
		return comment(name, Matchers.equalTo(comment));
	}

	/**
	 * Assert a cookie version with a {@link Matcher}
	 */
	public ResultMatcher version(final String name, final Matcher<? super Integer> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie version", cookie.getVersion(), matcher);
			}
		};
	}

	public ResultMatcher version(String name, int version) {
		return version(name, Matchers.equalTo(version));
	}

	/**
	 * Assert a cookie is secured
	 */
	public ResultMatcher secure(final String name, final boolean isSecure) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				Cookie cookie = result.getResponse().getCookie(name);
				MatcherAssert.assertThat("Response cookie secure", cookie.getSecure() == isSecure);
			}
		};
	}
}
