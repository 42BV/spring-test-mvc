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
package org.springframework.test.web.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Mock implementation of {@link ClientHttpResponse}.
 *
 * @author Craig Walls
 * @author Rossen Stoyanchev
 */
public class MockClientHttpResponse implements ClientHttpResponse {

	private InputStream body;

	private final HttpHeaders headers;

	private final HttpStatus status;

	public MockClientHttpResponse(byte[] body, HttpHeaders headers, HttpStatus statusCode) {
		this(bodyAsInputStream(body), headers, statusCode);
	}

	private static InputStream bodyAsInputStream(byte[] body) {
		return (body != null) ? new ByteArrayInputStream(body) : null;
	}

	public MockClientHttpResponse(InputStream body, HttpHeaders headers, HttpStatus statusCode) {
		this.body = body;
		this.headers = headers;
		this.status = statusCode;
	}

	public InputStream getBody() {
		return this.body;
	}

	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public HttpStatus getStatusCode() throws IOException {
		return this.status;
	}

	public String getStatusText() throws IOException {
		return this.status.getReasonPhrase();
	}

	public int getRawStatusCode() throws IOException {
		return this.status.value();
	}

	public void close() {
	}

}
