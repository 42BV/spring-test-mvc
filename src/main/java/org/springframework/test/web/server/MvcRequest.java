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

package org.springframework.test.web.server;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * A command class to build and execute a request. 
 * Use methods on {@link MockWebMvc} to obtain a new {@link MvcRequest} instance.
 * Once obtained an {@link MvcRequest} can be executed, modified, and executed again any number of times.
 * 
 */
public class MvcRequest {

	private URI uri;
	
	private HttpMethod method;
	
	private final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();

	private final Map<String, Object> headers = new LinkedHashMap<String, Object>();
	
	private byte[] requestBody;
	
	private Cookie[] cookies;

	private Locale locale;

	private String characterEncoding;
	
	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	private final Map<String, Object> sessionAttributes = new LinkedHashMap<String, Object>();

	private Principal principal;

	private final ServletContext servletContext;

	private final MockWebMvc mvcServer;

	/**
	 * Use methods on {@link MockWebMvc} to obtain a new instance.
	 */
	MvcRequest(MockWebMvc mvcServer, ServletContext servletContext, URI uri, HttpMethod method) {
		this.uri = uri;
		this.method = method;
		this.servletContext = servletContext;
		this.mvcServer = mvcServer;
	}

	public MvcRequest method(HttpMethod method) {
		this.method = method;
		return this;
	}
	
	public MvcRequest addParam(String name, String...values) {
		parameters.put(name, values);
		return this;
	}

	public MvcRequest removeParam(String name) {
		parameters.remove(name);
		return this;
	}

	public MvcRequest accept(MediaType...mediaTypes) {
		headers.put("Accept", MediaType.toString(Arrays.asList(mediaTypes)));
		return this;
	}

	public MvcRequest contentType(MediaType mediaType) {
		headers.put("Content-Type", mediaType.toString());
		return this;
	}
	
	public MvcRequest requestBody(String requestBody) {
		this.requestBody = requestBody.getBytes();
		return this;
	}
	
	public MvcRequest requestBody(byte[] requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public MvcRequest addHeader(String name, Object value) {
		headers.put(name, value);
		return this;
	}

	public MvcRequest removeHeader(String name) {
		headers.remove(name);
		return this;
	}

	public MvcRequest cookies(Cookie...cookies) {
		this.cookies = cookies;
		return this;
	}

	public MvcRequest locale(Locale locale) {
		this.locale = locale;
		return this;
	}

	public MvcRequest characterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		return this;
	}
	
	public MvcRequest addRequestAttr(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public MvcRequest removeRequestAttr(String name) {
		attributes.remove(name);
		return this;
	}

	public MvcRequest addSessionAttr(String name, Object value) {
		sessionAttributes.put(name, value);
		return this;
	}
	
	public MvcRequest removeSessionAttr(String name) {
		sessionAttributes.remove(name);
		return this;
	}

	public MvcRequest principal(Principal principal) {
		this.principal = principal;
		return this;
	}
	
	public MvcResultActions execute() {
		return mvcServer.execute(buildRequest(), new MockHttpServletResponse());
	}
	
	private MockHttpServletRequest buildRequest() {
		
		MockHttpServletRequest request = createServletRequest();
		
		request.setMethod(method.name());
		request.setRequestURI(uri.toString());
		
		for (String name : parameters.keySet()) {
			request.addParameter(name, parameters.get(name));
		}
		for (String name : headers.keySet()) {
			request.addHeader(name, headers.get(name));
		}
		for (String name : attributes.keySet()) {
			request.setAttribute(name, attributes.get(name));
		}
		for (String name : sessionAttributes.keySet()) {
			request.getSession().setAttribute(name, sessionAttributes.get(name));
		}
		
		request.setContent(requestBody);
		request.setCookies(cookies);
		request.setCharacterEncoding(characterEncoding);
		request.setUserPrincipal(principal);
		
		if (locale != null) {
			request.addPreferredLocale(locale);
		}
		
		return request;
	}

	protected MockHttpServletRequest createServletRequest() {
		return new MockHttpServletRequest(servletContext);
	}
	
	static interface MvcRequestExecutor {
		
		MvcResultActions execute(MockHttpServletRequest request, MockHttpServletResponse response);
	}

}
