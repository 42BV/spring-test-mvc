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

package org.springframework.test.web.server.result;

import java.util.Enumeration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * TODO ...
 */
public abstract class ResultHandlerUtils {

	/**
	 * TODO ...
	 */
	public static MultiValueMap<String, String> getRequestHeaderMap(MockHttpServletRequest request) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		Enumeration<?> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				map.add(name, values.nextElement());
			}
		}
		return map;
	}

	/**
	 * TODO ...
	 */
	public static MultiValueMap<String, String> getResponseHeaderMap(MockHttpServletResponse response) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		for (String name : response.getHeaderNames()) {
			headers.put(name, response.getHeaders(name));
		}
		return headers;
	}

}
