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

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.MockMvcResultMatcher;
import org.springframework.test.web.server.MockMvcResult;
import org.springframework.util.StringUtils;

/**
 * Response-related matchers
 *
 * @author Rossen Stoyanchev
 */
public class ResponseResultMatchers {
	
	ResponseResultMatchers() {
	}

	public MockMvcResultMatcher status(final int status) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Status", status, response.getStatus());
			}
		};
	}

	public MockMvcResultMatcher errorMessage(final String errorMessage) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Error message", errorMessage, response.getErrorMessage());
			}
		};
	}

	public MockMvcResultMatcher contentType(final String contentType) {
		return new MockMvcResultMatcher() {
			public void match(MockMvcResult result) {
				MockHttpServletResponse response = result.getResponse();
				if (StringUtils.hasText(response.getContentType())) {
					assertEquals("Content type", contentType, response.getContentType());
				}
				else {
					String headerName = "Content-Type";
					assertEquals("Content-Type response header", contentType, response.getHeader(headerName));
				}
			}
		};
	}

	public MockMvcResultMatcher body(final String content) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) throws UnsupportedEncodingException {
				assertEquals("Response body", content, response.getContentAsString());
			}
		};
	}

	public MockMvcResultMatcher responseBodyContains(final String text) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) throws UnsupportedEncodingException {
				String body = response.getContentAsString();
				assertTrue("Response body <" + body + "> does not contain " + text, body.contains(text));
			}
		};
	}

	public MockMvcResultMatcher responseBodyAsByteArray(final byte[] content) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Response body", content, response.getContentAsByteArray());
			}
		};
	}

	public MockMvcResultMatcher forwardedUrl(final String forwardUrl) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Forwarded URL", forwardUrl, response.getForwardedUrl());
			}
		};
	}

	public MockMvcResultMatcher redirectedUrl(final String redirectUrl) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Redirected URL", redirectUrl, response.getRedirectedUrl());
			}
		};
	}
	
	public MockMvcResultMatcher headersPresent(final String...headerNames) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response header", getHeaderValueMap(response), headerNames);
			}
		};
	}
	
	public MockMvcResultMatcher headersNotPresent(final String...headerNames) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesNotPresent("Response header", getHeaderValueMap(response), headerNames);
			}
		};
	}

	public MockMvcResultMatcher headerValue(final String headerName, final Object headerValue) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Response header", headerValue, response.getHeader(headerName));
			}
		};
	}

	public MockMvcResultMatcher headerValueContains(final String headerName, final String text) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response header", getHeaderValueMap(response), headerName);
				Object value = response.getHeader(headerName);
				assertEquals("Header value type", String.class, response.getHeader(headerName).getClass());
				assertTrue("Header '" + headerName + "' with value <" + value + "> does not contain <" + text + ">.",
						((String) value).contains(text));
			}
		};
	}

	public MockMvcResultMatcher cookiesPresent(final String...names) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesPresent("Response cookie", getCookieValueMap(response), names);
			}
		};
	}
	
	public MockMvcResultMatcher cookiesNotPresent(final String...names) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				AssertionErrors.assertNameValuesNotPresent("Response cookie", getCookieValueMap(response), names);
			}
		};
	}

	public MockMvcResultMatcher cookieValue(final String name, final String value) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
				assertEquals("Response cookie", value, response.getCookie(name).getValue());
			}
		};
	}

	public MockMvcResultMatcher cookieValueContains(final String cookieName, final String text) {
		return new MockResponseResultMatcher() {
			protected void matchMockResponse(MockHttpServletResponse response) {
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

	private static abstract class MockResponseResultMatcher implements MockMvcResultMatcher {
		
		public void match(MockMvcResult result) {
			try {
				matchMockResponse(result.getResponse());
			} catch (IOException e) {
				e.printStackTrace();
				AssertionErrors.fail("Failed mock response expectation: " + e.getMessage());
			}
		}
		
		protected abstract void matchMockResponse(MockHttpServletResponse response) throws IOException;
	}

}
