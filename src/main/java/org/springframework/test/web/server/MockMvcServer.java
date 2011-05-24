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

import javax.servlet.ServletContext;

import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriTemplate;

/**
 * <strong>Main entry point for server-side Spring MVC testing</strong>. 
 * 
 */
public class MockMvcServer {

	private final ServletContext servletContext;
	
	private final MvcDispatcher mvcDispatcher;

	private boolean mapOnly;
	
	/**
	 * To create a {@link MockMvcServer} see methods in {@code MvcServerBuilders}.
	 */
	MockMvcServer(ServletContext servletContext, MvcDispatcher mvcDispatcher) {
		this.servletContext = servletContext;
		this.mvcDispatcher = mvcDispatcher;
	}
	
	public void enableRequestMappingMode(boolean enable) {
		this.mapOnly = enable;
	}

	public MvcRequest get(String uriTemplate, Object...urlVariables) {
		return request(HttpMethod.GET, uriTemplate, urlVariables);
	}

	public MvcRequest post(String uriTemplate, Object...urlVariables) {
		return request(HttpMethod.POST, uriTemplate, urlVariables);
	}

	public MvcRequest put(String uriTemplate, Object...urlVariables) {
		return request(HttpMethod.PUT, uriTemplate, urlVariables);
	}
	
	public MvcRequest delete(String uriTemplate, Object...urlVariables) {
		return request(HttpMethod.DELETE, uriTemplate, urlVariables);
	}

	public MvcRequest request(HttpMethod method, String uriTemplate, Object...urlVariables) {
		URI uri= new UriTemplate(uriTemplate).expand(urlVariables);
		return new MvcRequest(this, servletContext, uri, method);
	}

	public MultipartMvcRequest multipartRequest(String uriTemplate, Object...urlVariables) {
		URI uri= new UriTemplate(uriTemplate).expand(urlVariables);
		return new MultipartMvcRequest(this, servletContext, uri);
	}

	protected MvcResultActions execute(MockHttpServletRequest request, MockHttpServletResponse response) {
		return mvcDispatcher.dispatch(request, response, mapOnly);
	}
	
}
