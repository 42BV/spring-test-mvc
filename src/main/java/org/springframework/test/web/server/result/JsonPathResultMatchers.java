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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.result.ServletResponseResultMatchers.ServletResponseResultMatcher;

/**
 * Provides methods to define expectations on the ServletResponse content
 * with <a href="http://code.google.com/p/json-path"/>JsonPath</a>.
 *
 * @author Rossen Stoyanchev
 */
public class JsonPathResultMatchers {

	private final String jsonPath;

	/**
	 * Protected constructor.
	 * @param jsonPath the JSON path to use in result matchers
	 * 
	 * @see MockMvcResultActions#response()
	 * @see ServletResponseResultMatchers#content()
	 * @see ContentResultMatchers#jsonPath(String)
	 */
	protected JsonPathResultMatchers(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	
	/**
	 * Assert there is content at the underlying JSON path.
	 */
	public ResultMatcher exists() {
		return result(Matchers.notNullValue());
	}

	/**
	 * Assert there is no content at the underlying JSON path.
	 */
	public ResultMatcher doesNotExist() {
		return result(Matchers.nullValue());
	}

	/**
	 * Extract the content at the underlying JSON path and assert it equals 
	 * the given Object. This is a shortcut {@link #result(Matcher)} with
	 * {@link Matchers#equalTo(Object)}.
	 */
	public ResultMatcher evaluatesTo(Object expectedContent) {
		return result(Matchers.equalTo(expectedContent));
	}
	
	/**
	 * Extract the content at the underlying JSON path and  assert it with 
	 * the given {@link Matcher}.
	 * <p>Example:
	 * <pre>
	 * // Assumes static import of org.hamcrest.Matchers.equalTo
	 * 
	 * mockMvc.perform(get("/path.json"))
	 *   .andExpect(response().content().jsonPath("$.store.bicycle.price").result(equalTo(19.95D)));
	 *  </pre>
	 */
	public <T> ResultMatcher result(final Matcher<T> matcher) {
		return new ServletResponseResultMatcher() {
			@SuppressWarnings("unchecked")
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				T extractedContent = (T) applyJsonPath(response.getContentAsString());
				MatcherAssert.assertThat("Response content JSON path: " + JsonPathResultMatchers.this.jsonPath, 
						extractedContent, matcher);
			}
		};
	}	

	/**
	 * Apply the underlying JSON path to the given content.
	 */
	protected Object applyJsonPath(String content) throws Exception {
		return com.jayway.jsonpath.JsonPath.read(content, this.jsonPath);
	}

}
