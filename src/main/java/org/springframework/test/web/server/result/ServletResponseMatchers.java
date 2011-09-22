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

import static org.springframework.test.web.AssertionErrors.assertEquals;
import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Matchers with expectations on the ServletResponse.
 *
 * @author Rossen Stoyanchev
 */
public class ServletResponseMatchers {
	
	ServletResponseMatchers() {
	}

    public ServletResponseStatusMatchers status() {
        return new ServletResponseStatusMatchers();
    }

	public ResultMatcher errorMessage(final String errorMessage) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Error message", errorMessage, response.getErrorMessage());
			}
		};
	}

	public ResultMatcher contentType(final MediaType mediaType) {
		return new MockHttpServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) {
				String value = response.getContentType();
				value = (value != null) ? value : response.getHeader("Content-Type"); 
				AssertionErrors.assertTrue("Content type not set", value != null);
				assertEquals("Content type", mediaType, MediaType.parseMediaType(value));
			}
		};
	}

	public ResultMatcher contentType(final String contentType) {
		return contentType(MediaType.valueOf(contentType));
	}

	public ResultMatcher characterEncoding(final String characterEncoding) {
		return new MockHttpServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) {
				assertEquals("Character encoding", characterEncoding, response.getCharacterEncoding());
			}
		};
	}
	
	public ResultMatcher body(final String content) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) throws UnsupportedEncodingException {
				assertEquals("Response body", content, response.getContentAsString());
			}
		};
	}

	public ResultMatcher bodyContains(final String text) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) throws UnsupportedEncodingException {
				String body = response.getContentAsString();
				assertTrue("Response body <" + body + "> does not contain " + text, body.contains(text));
			}
		};
	}

	public ResultMatcher body(final byte[] content) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Response body", content, response.getContentAsByteArray());
			}
		};
	}

	public ResultMatcher forwardedUrl(final String forwardUrl) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Forwarded URL", forwardUrl, response.getForwardedUrl());
			}
		};
	}

	public ResultMatcher redirectedUrl(final String redirectUrl) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Redirected URL", redirectUrl, response.getRedirectedUrl());
			}
		};
	}
	
	public ResultMatcher headersPresent(final String...headerNames) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response header", getHeaderValueMap(response), headerNames);
			}
		};
	}
	
	public ResultMatcher headersNotPresent(final String...headerNames) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesNotPresent("Response header", getHeaderValueMap(response), headerNames);
			}
		};
	}

	public ResultMatcher headerValue(final String headerName, final Object headerValue) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Response header", headerValue, response.getHeader(headerName));
			}
		};
	}

	public ResultMatcher headerValueContains(final String headerName, final String text) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response header", getHeaderValueMap(response), headerName);
				Object value = response.getHeader(headerName);
				assertEquals("Header value type", String.class, response.getHeader(headerName).getClass());
				assertTrue("Header '" + headerName + "' with value <" + value + "> does not contain <" + text + ">.",
						((String) value).contains(text));
			}
		};
	}

	public ResultMatcher cookiesPresent(final String...names) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response cookie", getCookieValueMap(response), names);
			}
		};
	}
	
	public ResultMatcher cookiesNotPresent(final String...names) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesNotPresent("Response cookie", getCookieValueMap(response), names);
			}
		};
	}

	public ResultMatcher cookieValue(final String name, final String value) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Response cookie", value, response.getCookie(name).getValue());
			}
		};
	}

	public ResultMatcher cookieValueContains(final String cookieName, final String text) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response cookie", getCookieValueMap(response), cookieName);
				String value = response.getCookie(cookieName).getValue();
				assertTrue("Cookie '" + cookieName + "' with value <" + value + "> does not contain <" + text + ">.", 
						value.contains(text));
			}
		};
	}

	static Map<String, Object> getHeaderValueMap(MockHttpServletResponse response) {
		Map<String, Object> headers = new LinkedHashMap<String, Object>();
		for (String name : response.getHeaderNames()) {
			headers.put(name, response.getHeader(name));
		}
		return headers;
	}

	static Map<String, Object> getCookieValueMap(MockHttpServletResponse response) {
		Map<String, Object> cookies = new LinkedHashMap<String, Object>();
		for (Cookie cookie : response.getCookies()) {
			cookies.put(cookie.getName(), cookie.getValue());
		}
		return cookies;
	}

}