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
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.AssertionErrors;
import org.springframework.util.Assert;

/**
 * Factory methods for {@link RequestMatcher} classes. Typically used to provide input for
 * {@link MockRestServiceServer#expect(RequestMatcher)}.
 *
 * @author Arjen Poutsma
 * @author Craig Walls
 */
public abstract class RequestMatchers {
	private RequestMatchers() {
	}

	/**
	 * Expects any request.
	 *
	 * @return the request matcher
	 */
	public static RequestMatcher anything() {
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
			}
		};
	}

	/**
	 * Expects the given {@link HttpMethod}.
	 *
	 * @param method the HTTP method
	 * @return the request matcher
	 */
	public static RequestMatcher method(final HttpMethod method) {
		Assert.notNull(method, "'method' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
				AssertionErrors.assertEquals("Unexpected HttpMethod", method, request.getMethod());
			}
		};
	}

	/**
	 * Expects a request to a URI string verified with the given matcher.
	 *
	 * @param uri the request URI
	 * @return the request matcher
	 */
	public static RequestMatcher requestTo(final Matcher<String> matcher) {
		Assert.notNull(matcher, "'matcher' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws IOException, AssertionError {
				MatcherAssert.assertThat("Request URI", request.getURI().toString(), matcher);
			}
		};
	}

	/**
	 * Expects a request to the given URI.
	 *
	 * @param uri the request URI
	 * @return the request matcher
	 */
	public static RequestMatcher requestTo(String uri) {
		Assert.notNull(uri, "'uri' must not be null");
		return requestTo(Matchers.equalTo(uri));
	}

	/**
	 * Expects a request to the given URI.
	 *
	 * @param uri the request URI
	 * @return the request matcher
	 */
	public static RequestMatcher requestTo(final URI uri) {
		Assert.notNull(uri, "'uri' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws IOException, AssertionError {
				AssertionErrors.assertEquals("Unexpected request", uri, request.getURI());
			}
		};
	}

	/**
	 * Expects the given request header
	 *
	 * @param header the header name
	 * @param value the header value
	 * @return the request matcher
	 */
	public static RequestMatcher header(final String header, final String... values) {
		Assert.notNull(header, "'header' must not be null");
		Assert.notEmpty(values, "'values' must not be empty");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
				List<String> actual = request.getHeaders().get(header);
				AssertionErrors.assertTrue("Expected header <" + header + "> in request", actual != null);
				for (String value : values) {
					AssertionErrors.assertTrue("Expected value <" + value + "> in header <" + header + ">",
							actual.contains(value));
				}
			}
		};
	}

	/**
	 * Expects that the specified request header contains a subtring
	 *
	 * @param header the header name
	 * @param substring the substring that must appear in the header
	 * @return the request matcher
	 */
	public static RequestMatcher headerContains(final String header, final String substring) {
		Assert.notNull(header, "'header' must not be null");
		Assert.notNull(substring, "'substring' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
				List<String> actualHeaders = request.getHeaders().get(header);
				AssertionErrors.assertTrue("Expected header <" + header + "> in request", actualHeaders != null);

				boolean foundMatch = false;
				for (String headerValue : actualHeaders) {
					if (headerValue.contains(substring)) {
						foundMatch = true;
						break;
					}
				}

				AssertionErrors.assertTrue("Expected value containing <" + substring + "> in header <" + header + ">",
						foundMatch);
			}
		};
	}

	/**
	 * Expects the given request body content
	 *
	 * @param body the request body
	 * @return the request matcher
	 */
	public static RequestMatcher body(final String body) {
		Assert.notNull(body, "'body' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError, IOException {
				MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
				AssertionErrors.assertEquals("Unexpected body content", body,
						mockRequest.getBodyContent());
			}
		};
	}
}
