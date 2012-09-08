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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@code ClientHttpRequestFactory} that maintains a list
 * of expected requests and returns each expected request whenever
 * {@link #createRequest(URI, HttpMethod)} is called.
 *
 * @author Craig Walls
 * @author Rossen Stoyanchev
 */
public class MockClientHttpRequestFactory implements ClientHttpRequestFactory {

	private final List<MockClientHttpRequest> expected = new LinkedList<MockClientHttpRequest>();

	private final List<MockClientHttpRequest> executed = new ArrayList<MockClientHttpRequest>();

	private Iterator<MockClientHttpRequest> iterator;


	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		Assert.notNull(uri, "'uri' must not be null");
		Assert.notNull(httpMethod, "'httpMethod' must not be null");

		initializeIterator();

		MockClientHttpRequest request = this.iterator.next();
		request.setUri(uri);
		request.setMethod(httpMethod);

		this.executed.add(request);

		return request;
	}

	private void initializeIterator() throws AssertionError {
		if (this.iterator == null) {
			this.iterator = this.expected.iterator();
		}
		if (!this.iterator.hasNext()) {
			throw new AssertionError("No further requests expected");
		}
	}

	MockClientHttpRequest expectRequest(RequestMatcher requestMatcher) {
		Assert.state(this.iterator == null, "Can't add more expectations when test is already underway");
		MockClientHttpRequest request = new MockClientHttpRequest(requestMatcher);
		this.expected.add(request);
		return request;
	}

	void verifyRequests() {
		if (this.expected.isEmpty() || this.expected.equals(this.executed)) {
			return;
		}
		throw new AssertionError(getVerifyMessage());
	}

	private String getVerifyMessage() {
		StringBuilder sb = new StringBuilder("Further request(s) expected\n");

		if (this.executed.size() > 0) {
			sb.append("The following ");
		}
		sb.append(this.executed.size()).append(" out of ");
		sb.append(this.expected.size()).append(" were executed");

		if (this.executed.size() > 0) {
			sb.append(":\n");
			for (MockClientHttpRequest request : this.executed) {
				sb.append(request.toString()).append("\n");
			}
		}

		return sb.toString();
	}

}
