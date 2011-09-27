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

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Miscellaneous utility methods used for result matching.
 *
 * @author Rossen Stoyanchev
 */
public class ResultMatcherUtils {

	public static Map<String, Object> requestHeadersAsMap(MockHttpServletRequest request) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<?> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, request.getHeader(name));
		}
		return map;
	}

	public static Map<String, Object> requestParamsAsMap(MockHttpServletRequest request) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement(); 
			String[] values = request.getParameterValues(name);
			result.put(name, (values != null) ? Arrays.asList(values) : null);
		}
		return result;
	}

	public static Map<String, String> headersAsMap(MockHttpServletResponse response) {
		Map<String, String> headers = new LinkedHashMap<String, String>();
		for (String name : response.getHeaderNames()) {
			headers.put(name, response.getHeader(name));
		}
		return headers;
	}

	public static Map<String, String> cookiesAsMap(MockHttpServletResponse response) {
		Map<String, String> cookies = new LinkedHashMap<String, String>();
		for (Cookie cookie : response.getCookies()) {
			cookies.put(cookie.getName(), cookie.getValue());
		}
		return cookies;
	}

	public static Document toDocument(String xml) throws ParserConfigurationException, SAXException, IOException  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		InputSource inputSource = new InputSource(new StringReader(xml));
		Document document = documentBuilder.parse(inputSource);
		return document;
	}
	
}
