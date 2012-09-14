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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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

	private final UriComponentsBuilder uriComponentsBuilder;

	private final HttpMethod method;

	private final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();

	private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<String, Object>();

	private String contentType;

	private byte[] content;

	private final List<Cookie> cookies = new ArrayList<Cookie>();

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
	 * Protected constructor. Use static factory methods in
	 * {@link MockMvcRequestBuilders}.
	 *
	 * @param uri the URI for the request including any component (e.g. scheme, host, query)
	 * @param httpMethod the HTTP method for the request
	 */
	protected DefaultRequestBuilder(URI uri, HttpMethod httpMethod) {
		Assert.notNull(uri, "uri is required");
		Assert.notNull(httpMethod, "httpMethod is required");
		this.uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
		this.method = httpMethod;
	}

	public DefaultRequestBuilder param(String name, String... values) {
		addToMultiValueMap(this.parameters, name, values);
		return this;
	}

	private static <T> void addToMultiValueMap(MultiValueMap<String, T> map, String name, T[] values) {
		Assert.hasLength(name, "'name' must not be empty");
		Assert.notNull(values, "'values' is required");
		Assert.notEmpty(values, "'values' must not be empty");
		for (T value : values) {
			map.add(name, value);
		}
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

	public DefaultRequestBuilder body(byte[] content) {
		this.content = content;
		return this;
	}

	public DefaultRequestBuilder header(String name, Object... values) {
		addToMultiValueMap(this.headers, name, values);
		return this;
	}

	public DefaultRequestBuilder headers(HttpHeaders httpHeaders) {
		for (String name : httpHeaders.keySet()) {
			for (String value : httpHeaders.get(name)) {
				this.headers.add(name, value);
			}
		}
		return this;
	}

	public DefaultRequestBuilder cookie(Cookie... cookies) {
		Assert.notNull(cookies, "'cookies' must not be null");
		Assert.notEmpty(cookies, "'cookies' must not be empty");
		this.cookies.addAll(Arrays.asList(cookies));
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

	/**
	 * Specify the portion of the requestURI that indicates the request context.
	 * The request URI must begin with the context path. It should start with a
	 * "/" but must not end with a "/".
	 *
	 * @see <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getContextPath%28%29">HttpServletRequest.getContextPath()</a>
	 */
	public DefaultRequestBuilder contextPath(String contextPath) {
		if (StringUtils.hasText(contextPath)) {
			Assert.isTrue(contextPath.startsWith("/"), "Context path must start with a '/'");
			Assert.isTrue(!contextPath.endsWith("/"), "Context path must not end with a '/'");
		}
		this.contextPath = (contextPath != null) ? contextPath : "";
		return this;
	}

	/**
	 * Specify the portion of the requestURI that represents the Servlet path.
	 * The request URI must begin with the context path, followed by the Servlet path.
	 * The pathInfo is the remaining portion of the requestURI.
	 *
	 * @see <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getServletPath%28%29">HttpServletRequest.getServletPath()</a>
	 */
	public DefaultRequestBuilder servletPath(String servletPath) {
		if (StringUtils.hasText(servletPath)) {
			Assert.isTrue(servletPath.startsWith("/"), "Servlet path must start with a '/'");
			Assert.isTrue(!servletPath.endsWith("/"), "Servlet path must not end with a '/'");
		}
		this.servletPath = (servletPath != null) ? servletPath : "";
		return this;
	}

	public DefaultRequestBuilder secure(boolean secure){
		this.secure = secure;
		return this;
	}

	public MockHttpServletRequest buildRequest(ServletContext servletContext) {

		MockHttpServletRequest request = createServletRequest(servletContext);

		UriComponents uriComponents = this.uriComponentsBuilder.build();

		String requestUri = uriComponents.getPath();
		request.setRequestURI(requestUri);

		Assert.isTrue(requestUri.startsWith(this.contextPath),
				"requestURI [" + requestUri + "] does not start with contextPath [" + this.contextPath + "]");

		Assert.isTrue(requestUri.startsWith(this.contextPath + this.servletPath),
				"Invalid servletPath [" + this.servletPath + "] for requestURI [" + requestUri + "]");

		request.setContextPath(this.contextPath);
		request.setServletPath(this.servletPath);
		request.setPathInfo(derivePathInfo(requestUri));

		if (uriComponents.getScheme() != null) {
			request.setScheme(uriComponents.getScheme());
		}
		if (uriComponents.getHost() != null) {
			request.setServerName(uriComponents.getHost());
		}
		if (uriComponents.getPort() != -1) {
			request.setServerPort(uriComponents.getPort());
		}

		request.setMethod(this.method.name());

		for (String name : this.headers.keySet()) {
			for (Object value : this.headers.get(name)) {
				request.addHeader(name, value);
			}
		}

		request.setQueryString(uriComponents.getQuery());

		for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
			for (String value : entry.getValue()) {
				request.addParameter(entry.getKey(), value);
			}
		}

		for (String name : this.parameters.keySet()) {
			for (String value : this.parameters.get(name)) {
				request.addParameter(name, value);
			}
		}

		for (String name : this.attributes.keySet()) {
			request.setAttribute(name, this.attributes.get(name));
		}

		// Set session before session attributes

		if (this.session != null) {
			request.setSession(this.session);
		}

		for (String name : this.sessionAttributes.keySet()) {
			request.getSession().setAttribute(name, this.sessionAttributes.get(name));
		}

		request.setContentType(this.contentType);
		request.setContent(this.content);
		request.setCookies(this.cookies.toArray(new Cookie[this.cookies.size()]));
		request.setCharacterEncoding(this.characterEncoding);
		request.setUserPrincipal(this.principal);
		request.setSecure(this.secure);

		if (this.locale != null) {
			request.addPreferredLocale(this.locale);
		}

		return request;
	}

	/**
	 * Creates a new {@link MockHttpServletRequest} based on the given
	 * {@link ServletContext}. Can be overridden in sub-classes.
	 */
	protected MockHttpServletRequest createServletRequest(ServletContext servletContext) {
		return new MockHttpServletRequest(servletContext);
	}

	private String derivePathInfo(String requestUri) {
		if (!StringUtils.hasText(this.contextPath) && !StringUtils.hasText(this.servletPath)) {
			return null;
		}
		String pathInfo = requestUri.substring(this.contextPath.length() + this.servletPath.length());
		if (!StringUtils.hasText(pathInfo)) {
			return null;
		}
		return pathInfo;
	}

}
