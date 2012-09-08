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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.server.RequestBuilder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

	private MockHttpSession session;

	private Principal principal;

	private String contextPath = "";

	private String servletPath = "";

	private boolean secure = false;


	/**
	 * Use methods on {@link MockMvcRequestBuilders} to obtain a new instance.
	 */
	DefaultRequestBuilder(URI uri, HttpMethod method) {
		this.uri = uri;
		this.method = method;
	}

	public DefaultRequestBuilder param(String name, String value, String... values) {
		addToMultiValueMap(this.parameters, name, value, values);
		return this;
	}

	public DefaultRequestBuilder accept(MediaType... mediaTypes) {
		Assert.notEmpty(mediaTypes, "No 'Accept' media types");
		this.headers.set("Accept", MediaType.toString(Arrays.asList(mediaTypes)));
		return this;
	}

	public DefaultRequestBuilder contentType(MediaType mediaType) {
		Assert.notNull(mediaType, "'mediaType' must not be null");
		this.contentType = mediaType.toString();
		this.headers.set("Content-Type", this.contentType);
		return this;
	}

	public DefaultRequestBuilder body(byte[] requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public DefaultRequestBuilder header(String name, Object value, Object... values) {
		addToMultiValueMap(this.headers, name, value, values);
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
		this.attributes.put(name, value);
		return this;
	}

	public DefaultRequestBuilder sessionAttr(String name, Object value) {
		Assert.hasLength(name, "'name' must not be empty");
		Assert.notNull(value, "'value' must not be null");
		this.sessionAttributes.put(name, value);
		return this;
	}

	public DefaultRequestBuilder sessionAttrs(Map<String, Object> attrs) {
		Assert.notNull(attrs, "'attrs' must not be null");
		this.sessionAttributes.putAll(attrs);
		return this;
	}

	/**
	 * Provide an MockHttpSession instance to use, possibly for re-use across tests.
	 * Attributes provided via {@link #sessionAttr(String, Object)} and
	 * {@link #sessionAttrs(Map)} will override attributes in the provided session.
	 */
	public DefaultRequestBuilder session(MockHttpSession session) {
		Assert.notNull(session, "'session' must not be null");
		this.session = session;
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

	/**
	 * Set the servletPath to which the DispatcherServlet is mapped.
	 * When specified, pathInfo will be equal to the remaining part of the URI.
	 * <p>For example given a servletPath of {@code "/main"} and request URL
	 * {@code "/main/accounts/1"}, the pathInfo will be {@code "/accounts/1"}.
	 * Or if the servletPath is not set, the pathInfo will be the full URL.
	 */
	public DefaultRequestBuilder servletPath(String servletPath) {
		this.servletPath = servletPath;
		return this;
	}

	public DefaultRequestBuilder secure(boolean secure){
		this.secure = secure;
		return this;
	}

	public MockHttpServletRequest buildRequest(ServletContext servletContext) {

		MockHttpServletRequest request = createServletRequest(servletContext);

		request.setMethod(this.method.name());

		String requestUri = UriComponentsBuilder.fromUri(this.uri).query(null).fragment(null).build().toString();
		request.setRequestURI(requestUri);

		UriComponents uriComponents = UriComponentsBuilder.fromUri(this.uri).build();
		String queryString = uriComponents.getQuery();
		request.setQueryString(queryString);

		MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
		for (String name : queryParams.keySet()) {
			for (String value : queryParams.get(name)) {
				request.addParameter(name, value);
			}
		}

		for (String name : this.parameters.keySet()) {
			for (String value : this.parameters.get(name)) {
				request.addParameter(name, value);
			}
		}
		for (String name : this.headers.keySet()) {
			for (Object value : this.headers.get(name)) {
				request.addHeader(name, value);
			}
		}
		for (String name : this.httpHeaders.keySet()) {
			for (Object value : this.httpHeaders.get(name)) {
				request.addHeader(name, value);
			}
		}
		for (String name : this.attributes.keySet()) {
			request.setAttribute(name, this.attributes.get(name));
		}

		if (this.session != null) {
			request.setSession(this.session);
		}
		for (String name : this.sessionAttributes.keySet()) {
			request.getSession().setAttribute(name, this.sessionAttributes.get(name));
		}

		request.setContentType(this.contentType);
		request.setContent(this.requestBody);
		request.setCookies(this.cookies);
		request.setCharacterEncoding(this.characterEncoding);
		request.setUserPrincipal(this.principal);
		request.setContextPath(this.contextPath);
		request.setServletPath(this.servletPath);
		request.setPathInfo(determinePathInfo());
		request.setSecure(this.secure);

		if (this.locale != null) {
			request.addPreferredLocale(this.locale);
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

	private String determinePathInfo() {
		String uriString = this.uri.toString();
		String prefix = "";
		if (StringUtils.hasText(this.contextPath)) {
			prefix += this.contextPath;
			Assert.isTrue(uriString.startsWith(prefix), "The URI '" + uriString
					+ "' must start with the contextPath='" + prefix + "'");
		}
		if (StringUtils.hasText(this.servletPath)) {
			prefix += this.servletPath;
			Assert.isTrue(uriString.startsWith(prefix), "The URI '" + uriString
					+ "' must start with the combined contextPath and servletPath '" + prefix + "'");
		}
		return uriString.substring(prefix.length());
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
