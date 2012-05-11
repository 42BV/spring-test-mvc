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

package org.springframework.test.web.server.request;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.server.RequestBuilder;
import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The default builder for {@link MockHttpServletRequest}.
 * 
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 */
public class DefaultRequestBuilder implements RequestBuilder {

	private final URI uri;

	private final HttpMethod method;

	private final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();

	private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<String, Object>();

	private final HttpHeaders httpHeaders = new HttpHeaders();

	private String contentType;

	private byte[] requestBody;

	private Cookie[] cookies;

	private Locale locale;

	private String characterEncoding;

	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	private final Map<String, Object> sessionAttributes = new LinkedHashMap<String, Object>();

	private Principal principal;

	private String contextPath = "";

	private String servletPath = "";

	private String pathInfo;
	
	private boolean secure = false;

	/** Use methods on {@link MockMvc} to obtain a new instance. */
	DefaultRequestBuilder(URI uri, HttpMethod method) {
		this.uri = uri;
		this.method = method;
	}

	public DefaultRequestBuilder param(String name, String value, String... values) {
		addToMultiValueMap(parameters, name, value, values);
		return this;
	}

	public DefaultRequestBuilder accept(MediaType... mediaTypes) {
		Assert.notEmpty(mediaTypes, "No 'Accept' media types");
		headers.set("Accept", MediaType.toString(Arrays.asList(mediaTypes)));
		return this;
	}

	public DefaultRequestBuilder contentType(MediaType mediaType) {
		Assert.notNull(mediaType, "'mediaType' must not be null");
		this.contentType = mediaType.toString();
		headers.set("Content-Type", this.contentType);
		return this;
	}

	public DefaultRequestBuilder body(byte[] requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public DefaultRequestBuilder header(String name, Object value, Object... values) {
		addToMultiValueMap(headers, name, value, values);
		return this;
	}

	public DefaultRequestBuilder headers(HttpHeaders httpHeaders) {
		this.httpHeaders.putAll(httpHeaders);
		return this;
	}

	public DefaultRequestBuilder cookie(Cookie cookie, Cookie... cookies) {
		Assert.notNull(cookie, "'cookie' must not be null");
		if (cookies == null) {
			this.cookies = new Cookie[] { cookie };
		}
		else {
			this.cookies = new Cookie[1 + cookies.length];
			this.cookies[0] = cookie;
			System.arraycopy(cookies, 0, this.cookies, 1, cookies.length);
		}
		return this;
	}

	public DefaultRequestBuilder locale(Locale locale) {
		this.locale = locale;
		return this;
	}

	public DefaultRequestBuilder characterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		return this;
	}

	public DefaultRequestBuilder requestAttr(String name, Object value) {
		Assert.hasLength(name, "'name' must not be empty");
		Assert.notNull(value, "'value' must not be null");
		attributes.put(name, value);
		return this;
	}

	public DefaultRequestBuilder sessionAttr(String name, Object value) {
		Assert.hasLength(name, "'name' must not be empty");
		Assert.notNull(value, "'value' must not be null");
		sessionAttributes.put(name, value);
		return this;
	}

	public DefaultRequestBuilder principal(Principal principal) {
		Assert.notNull(principal, "'principal' must not be null");
		this.principal = principal;
		return this;
	}

	public DefaultRequestBuilder contextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public DefaultRequestBuilder servletPath(String servletPath) {
		this.servletPath = servletPath;
		return this;
	}

	public DefaultRequestBuilder pathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
		return this;
	}
	
	public DefaultRequestBuilder secure(boolean secure){
		this.secure = secure;
		return this;
	}

	public MockHttpServletRequest buildRequest(ServletContext servletContext) {

		MockHttpServletRequest request = createServletRequest(servletContext);

		request.setMethod(method.name());
		request.setRequestURI(uri.toString());

		for (String name : parameters.keySet()) {
			for (String value : parameters.get(name)) {
				request.addParameter(name, value);
			}
		}
		for (String name : headers.keySet()) {
			for (Object value : headers.get(name)) {
				request.addHeader(name, value);
			}
		}
		for (String name : httpHeaders.keySet()) {
			for (Object value : httpHeaders.get(name)) {
				request.addHeader(name, value);
			}
		}
		for (String name : attributes.keySet()) {
			request.setAttribute(name, attributes.get(name));
		}
		for (String name : sessionAttributes.keySet()) {
			request.getSession().setAttribute(name, sessionAttributes.get(name));
		}

		request.setContentType(contentType);
		request.setContent(requestBody);
		request.setCookies(cookies);
		request.setCharacterEncoding(characterEncoding);
		request.setUserPrincipal(principal);
		request.setContextPath(contextPath);
		request.setServletPath(servletPath);
		request.setPathInfo(pathInfo);
		request.setSecure(secure);

		if (locale != null) {
			request.addPreferredLocale(locale);
		}

		return request;
	}

	/**
	 * Creates a new {@link MockHttpServletRequest} based on the given {@link ServletContext}. Can be overridden in
	 * subclasses.
	 *
	 * @param servletContext the servlet context to use
	 * @return the created mock request
	 */
	protected MockHttpServletRequest createServletRequest(ServletContext servletContext) {
		return new MockHttpServletRequest(servletContext);
	}

	private static <T> void addToMultiValueMap(MultiValueMap<String, T> map, String name, T value, T[] values) {
		Assert.hasLength(name, "'name' must not be empty");
		Assert.notNull(value, "'value' must not be null");
		map.add(name, value);
		if (values != null) {
			map.get(name).addAll(Arrays.asList(values));
		}
	}
}
