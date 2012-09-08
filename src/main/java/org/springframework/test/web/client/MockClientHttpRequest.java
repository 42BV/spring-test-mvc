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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@code ClientHttpRequest} that maintains a list of
 * request expectations, in the form of {@link RequestMatcher}'s, as well as one
 * {@link ResponseCreator}. When {@link #execute()} is invoked, each request
 * matcher is invoked to verify the expectations. If all expectations are met,
 * a response is created with {@code ResponseCreator} and is then returned.
 *
 * <p>This class is also an implementation of {@link ResponseActions} to form a
 * fluent API for adding {@link RequestMatcher}'s and a {@code ResponseCreator}.
 *
 * @author Craig Walls
 * @author Rossen Stoyanchev
 */
public class MockClientHttpRequest implements ClientHttpRequest, ResponseActions {

	private URI uri;

	private HttpMethod httpMethod;

	private HttpHeaders httpHeaders = new HttpHeaders();

	private ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();

	private final List<RequestMatcher> requestMatchers = new LinkedList<RequestMatcher>();

	private ResponseCreator responseCreator;


	public MockClientHttpRequest(RequestMatcher requestMatcher) {
		Assert.notNull(requestMatcher, "RequestMatcher is required");
		this.requestMatchers.add(requestMatcher);
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI getURI() {
		return this.uri;
	}

	public void setMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public HttpMethod getMethod() {
		return this.httpMethod;
	}

	public HttpHeaders getHeaders() {
		return this.httpHeaders;
	}

	public OutputStream getBody() throws IOException {
		return this.bodyStream;
	}

	public String getBodyAsString() throws IOException {
		return this.bodyStream.toString("UTF-8");
	}

	public byte[] getBodyAsByteArray() throws IOException {
		return this.bodyStream.toByteArray();
	}

	public ClientHttpResponse execute() throws IOException {

		if (this.requestMatchers.isEmpty()) {
			throw new AssertionError("No request expectations to execute");
		}

		if (this.responseCreator == null) {
			throw new AssertionError("No ResponseCreator was set up. Add it after request expectations, "
					+ "e.g. MockRestServiceServer.expect(requestTo(\"/foo\")).andRespond(withSuccess())");
		}

		for (RequestMatcher requestMatcher : this.requestMatchers) {
			requestMatcher.match(this);
		}

		return this.responseCreator.createResponse(this);
	}

	// ResponseActions implementation

	public ResponseActions andExpect(RequestMatcher requestMatcher) {
		Assert.notNull(requestMatcher, "RequestMatcher is required");
		this.requestMatchers.add(requestMatcher);
		return this;
	}

	public void andRespond(ResponseCreator responseCreator) {
		Assert.notNull(responseCreator, "ResponseCreator is required");
		this.responseCreator = responseCreator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.httpMethod != null) {
			sb.append(this.httpMethod);
		}
		if (this.uri != null) {
			sb.append(" ").append(this.uri);
		}
		if (!this.httpHeaders.isEmpty()) {
			sb.append(", headers : ").append(this.httpHeaders);
		}
		if (sb.length() == 0) {
			sb.append("Not yet initialized");
		}
		return sb.toString();
	}

}
