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
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * A command class to build and execute a request. Use methods on {@link MockMvc} to obtain a new {@link
 * DefaultMockHttpServletRequestBuilder} instance.
 */
public class DefaultMockHttpServletRequestBuilder implements MockHttpServletRequestBuilder {

    private final ServletContext servletContext;

    private final URI uri;

    private final HttpMethod method;

    private final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();

    private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<String, Object>();

    private String contentType;

    private byte[] requestBody;

    private Cookie[] cookies;

    private Locale locale;

    private String characterEncoding;

    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

    private final Map<String, Object> sessionAttributes = new LinkedHashMap<String, Object>();

    private Principal principal;


    /** Use methods on {@link MockMvc} to obtain a new instance. */
    DefaultMockHttpServletRequestBuilder(ServletContext servletContext, URI uri, HttpMethod method) {
        this.uri = uri;
        this.method = method;
        this.servletContext = servletContext;
    }

    public DefaultMockHttpServletRequestBuilder param(String name, String value, String... values) {
        addToMultiValueMap(parameters, name, value, values);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder accept(MediaType mediaType, MediaType... mediaTypes) {
        addToMultiValueMap(headers, "Accept", mediaType, mediaTypes);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder contentType(MediaType mediaType) {
        Assert.notNull(mediaType, "'mediaType' must not be null");
        this.contentType = mediaType.toString();
        headers.set("Content-Type", mediaType);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder body(byte[] requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public DefaultMockHttpServletRequestBuilder header(String name, Object value, Object... values) {
        addToMultiValueMap(headers, name, value, values);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder cookie(Cookie cookie, Cookie... cookies) {
        Assert.notNull(cookie, "'cookie' must not be null");
        if (cookies == null) {
            this.cookies = new Cookie[]{cookie};
        }
        else {
            this.cookies = new Cookie[1 + cookies.length];
            this.cookies[0] = cookie;
            System.arraycopy(cookies, 0, this.cookies, 1, cookies.length);
        }
        return this;
    }

    public DefaultMockHttpServletRequestBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public DefaultMockHttpServletRequestBuilder characterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        return this;
    }

    public DefaultMockHttpServletRequestBuilder requestAttr(String name, Object value) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(value, "'value' must not be null");
        attributes.put(name, value);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder sessionAttr(String name, Object value) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(value, "'value' must not be null");
        sessionAttributes.put(name, value);
        return this;
    }

    public DefaultMockHttpServletRequestBuilder principal(Principal principal) {
        Assert.notNull(principal, "'principal' must not be null");
        this.principal = principal;
        return this;
    }

    public MockHttpServletRequest buildRequest() {

        MockHttpServletRequest request = createServletRequest();

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

        if (locale != null) {
            request.addPreferredLocale(locale);
        }

        return request;
    }

    protected MockHttpServletRequest createServletRequest() {
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
