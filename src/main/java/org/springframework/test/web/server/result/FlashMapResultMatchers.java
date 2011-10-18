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
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.FlashMap;

/**
 * Provides methods to define expectations on the "output" FlashMap.
 *
 * @author Rossen Stoyanchev
 */
public class FlashMapResultMatchers {

	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#view()
	 */
	protected FlashMapResultMatchers() {
	}

	public ResultMatcher attribute(final String attributeName, final Object attributeValue) {
		return attribute(attributeName, Matchers.equalTo(attributeValue));
	}

	public ResultMatcher attribute(final String name, final Matcher<Object> matcher) {
		return new AbstractFlashMapResultMatcher() {
			public void matchFlashMap(FlashMap flashMap) throws Exception {
				MatcherAssert.assertThat("FlashMap attribute", flashMap.get(name), matcher);
			}
		};
	}

	public ResultMatcher size(final int size) {
		return new AbstractFlashMapResultMatcher() {
			public void matchFlashMap(FlashMap flashMap) {
				AssertionErrors.assertEquals("Model size", size, flashMap.size());
			}
		};
	}

}
