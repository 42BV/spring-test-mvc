/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.test.web.client;

import static org.hamcrest.Matchers.containsString;

import java.net.URI;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

public class RequestMatchersTest {

	private MockClientHttpRequest request;

	@Before
	public void setUp() {
		request = new MockClientHttpRequest();
	}

	@Test
	public void requestTo() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		RequestMatchers.requestTo("http://foo.com/bar").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void requestTo_doesNotMatch() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		RequestMatchers.requestTo("http://foo.com/wrong").match(this.request);
	}

	@Test
	public void requestToContains() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		RequestMatchers.requestTo(containsString("bar")).match(this.request);
	}

	@Test
	public void method() throws Exception {
		request.setHttpMethod(HttpMethod.GET);

		RequestMatchers.method(HttpMethod.GET).match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void method_doesNotMatch() throws Exception {
		request.setHttpMethod(HttpMethod.POST);

		RequestMatchers.method(HttpMethod.GET).match(this.request);
	}

	@Test
	public void header() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar", "baz"));

		RequestMatchers.header("foo", "bar").match(this.request);
		RequestMatchers.header("foo", "baz").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void header_withMissingHeader() throws Exception {
		RequestMatchers.header("foo", "bar").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void header_withMissingValue() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar", "baz"));

		RequestMatchers.header("foo", "bad").match(this.request);
	}

	@Test
	public void headerContains() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar", "baz"));

		RequestMatchers.headerContains("foo", "ba").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void headerContains_withMissingHeader() throws Exception {
		RequestMatchers.headerContains("foo", "baz").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void headerContains_withMissingValue() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar", "baz"));

		RequestMatchers.headerContains("foo", "bx").match(this.request);
	}

	@Test
	public void headers() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar", "baz"));

		RequestMatchers.header("foo", "bar", "baz").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void headers_withMissingHeader() throws Exception {
		RequestMatchers.header("foo", "bar").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void headers_withMissingValue() throws Exception {
		request.getHeaders().put("foo", Arrays.asList("bar"));

		RequestMatchers.header("foo", "bar", "baz").match(this.request);
	}

	@Test
	public void body() throws Exception {
		request.getBody().write("test".getBytes());

		RequestMatchers.body("test").match(this.request);
	}

	@Test(expected=AssertionError.class)
	public void body_notEqual() throws Exception {
		request.getBody().write("test".getBytes());

		RequestMatchers.body("Test").match(this.request);
	}

}