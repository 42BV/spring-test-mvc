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
import java.nio.charset.Charset;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;

/**
 * Provides static methods with different ways to prepare a {@link ResponseCreator} instance.
 *
 * @author Rossen Stoyanchev
 */
public abstract class ResponseCreators {

	private ResponseCreators() {
	}

	/**
	 * Factory method for a 200 (OK) response without a body.
	 */
	public static DefaultResponseCreator withSuccess() {
		return new DefaultResponseCreator(HttpStatus.OK);
	}

	/**
	 * Factory method for a 200 (OK) response with content.
	 * @param content the response content, a "UTF-8" string
	 * @param mediaType the type of the content, may be {@code null}
	 */
	public static DefaultResponseCreator withSuccess(String content, MediaType mediaType) {
		return new DefaultResponseCreator(HttpStatus.OK).body(content).contentType(mediaType);
	}

	/**
	 * Factory method for a 200 (OK) response with content.
	 * @param content the response content from a byte array
	 * @param mediaType the type of the content, may be {@code null}
	 */
	public static DefaultResponseCreator withSuccess(byte[] content, MediaType contentType) {
		return new DefaultResponseCreator(HttpStatus.OK).body(content).contentType(contentType);
	}

	/**
	 * Factory method for a 200 (OK) response with content.
	 * @param content the response content from a {@link Resource}
	 * @param mediaType the type of the content, may be {@code null}
	 */
	public static DefaultResponseCreator withSuccess(Resource content, MediaType contentType) {
		return new DefaultResponseCreator(HttpStatus.OK).body(content).contentType(contentType);
	}

	/**
	 * Factory method for a 201 (CREATED) response with a {@code Location} header.
	 * @param location the value for the {@code Location} header
	 */
	public static DefaultResponseCreator withCreatedEntity(URI location) {
		return new DefaultResponseCreator(HttpStatus.CREATED).location(location);
	}

	/**
	 * Factory method for a 204 (NO_CONTENT) response.
	 */
	public static DefaultResponseCreator withNoContent() {
		return new DefaultResponseCreator(HttpStatus.NO_CONTENT);
	}

	/**
	 * Factory method for a 400 (BAD_REQUEST) response.
	 */
	public static DefaultResponseCreator withBadRequest() {
		return new DefaultResponseCreator(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Factory method for a 401 (UNAUTHORIZED) response.
	 */
	public static DefaultResponseCreator withUnauthorizedRequest() {
		return new DefaultResponseCreator(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Factory method for a 500 (SERVER_ERROR) response.
	 */
	public static DefaultResponseCreator withServerError() {
		return new DefaultResponseCreator(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static DefaultResponseCreator withStatus(HttpStatus status) {
		return new DefaultResponseCreator(status);
	}

	/**
	 * Respond with a given body, headers, status code, and status text.
	 *
	 * @param body the body of the response "UTF-8" encoded
	 * @param headers the response headers
	 * @param statusCode the response status code
	 * @param statusText the response status text
	 *
	 * @deprecated in favor of methods returning DefaultResponseCreator
	 */
	public static ResponseCreator withResponse(final String body, final HttpHeaders headers,
			final HttpStatus statusCode, final String statusText) {

		return new ResponseCreator() {
			public MockClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
				return new MockClientHttpResponse(body.getBytes(Charset.forName("UTF-8")), headers, statusCode);
			}
		};
	}

	/**
	 * Respond with the given body, headers, and a status code of 200 (OK).
	 *
	 * @param body the body of the response "UTF-8" encoded
	 * @param headers the response headers
	 *
	 * @deprecated in favor of methods returning DefaultResponseCreator
	 */
	public static ResponseCreator withResponse(String body, HttpHeaders headers) {
		return withResponse(body, headers, HttpStatus.OK, "");
	}

	/**
	 * Respond with a given body, headers, status code, and text.
	 *
	 * @param body a {@link Resource} containing the body of the response
	 * @param headers the response headers
	 * @param statusCode the response status code
	 * @param statusText the response status text
	 *
	 * @deprecated in favor of methods returning DefaultResponseCreator
	 */
	public static ResponseCreator withResponse(final Resource body, final HttpHeaders headers,
			final HttpStatus statusCode, String statusText) {

		return new ResponseCreator() {
			public MockClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
				return new MockClientHttpResponse(body.getInputStream(), headers, HttpStatus.OK);
			}
		};
	}

	/**
	 * Respond with the given body, headers, and a status code of 200 (OK).
	 * @param body the body of the response
	 * @param headers the response headers
	 *
	 * @deprecated in favor of methods returning DefaultResponseCreator
	 */
	public static ResponseCreator withResponse(final Resource body, final HttpHeaders headers) {
		return withResponse(body, headers, HttpStatus.OK, "");
	}

}
