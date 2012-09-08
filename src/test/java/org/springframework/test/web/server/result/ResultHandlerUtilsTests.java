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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.MultiValueMap;

/**
 * Tests for {@link ResultHandlerUtils}.
 *
 * @author Rossen Stoyanchev
 */
public class ResultHandlerUtilsTests {

	@Test
	public void testGetRequestHeaderMap() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("foo", "value1");
		request.addHeader("foo", "value2");
		request.addHeader("foo", "value3");
		request.addHeader("bar", "baz");
		
		MultiValueMap<String, String> map = ResultHandlerUtils.getRequestHeaderMap(request);
		
		assertEquals(2, map.size());
		assertEquals(Arrays.asList("value1", "value2", "value3"), map.get("foo"));
		assertEquals(Arrays.asList("baz"), map.get("bar"));
	}

	@Test
	public void testGetResponseHeaderMap() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.addHeader("foo", "value1");
		response.addHeader("foo", "value2");
		response.addHeader("foo", "value3");
		response.addHeader("bar", "baz");
		
		MultiValueMap<String, String> map = ResultHandlerUtils.getResponseHeaderMap(response);
		
		assertEquals(2, map.size());
		assertEquals(Arrays.asList("value1", "value2", "value3"), map.get("foo"));
		assertEquals(Arrays.asList("baz"), map.get("bar"));
	}

}
