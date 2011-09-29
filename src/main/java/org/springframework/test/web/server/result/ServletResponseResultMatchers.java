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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Provides methods to define expectations on the HttpServletResponse.
 *
 * @author Rossen Stoyanchev
 */
public class ServletResponseResultMatchers {
	
	private ContentResultMatchers contentMatchers = new ContentResultMatchers();

    private StatusResultMatchers statusMatchers = new StatusResultMatchers();
	
	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#response()
	 */
	protected ServletResponseResultMatchers() {
	}
	
	/**
	 * Set the {@link ServletResponseResultMatchers} instance to return 
	 * from {@link #content()};
	 */
	public void setContentResultMatchers(ContentResultMatchers contentMatchers) {
		this.contentMatchers = contentMatchers;
	}

    /**
     * Set the {@link StatusResultMatchers} instance to return
     * form {@link #status()}
     * @param statusMatchers
     */
    public void setStatusMatchers(StatusResultMatchers statusMatchers) {
        this.statusMatchers = statusMatchers;
    }

    public StatusResultMatchers status(){
        return this.statusMatchers;
    }

    /**
	 * Match the expected response status to that of the HttpServletResponse
	 */
	public ResultMatcher status(final HttpStatus expectedStatus) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", expectedStatus, HttpStatus.valueOf(response.getStatus()));
			}
		};
	}

	/**
	 * Match the expected response status and reason to those set in the HttpServletResponse
	 * via {@link HttpServletResponse#sendError(int, String)}. 
	 */
	public ResultMatcher statusAndReason(final HttpStatus expectedStatus, final String expectedReason) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", expectedStatus, HttpStatus.valueOf(response.getStatus()));
				assertEquals("Reason", expectedReason, response.getErrorMessage());
			}
		};
	}

	/**
	 * Obtain the response content type looking it up in the ServletResponse first and 
	 * in the 'Content-Type' response header second. Parse the resulting String into a 
	 * MediaType and compare it to the {@code expectedContentType}.
	 */
	public ResultMatcher contentType(final MediaType expectedContentType) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) {
				String value = response.getContentType();
				value = (value != null) ? value : response.getHeader("Content-Type"); 
				AssertionErrors.assertTrue("Content type not set", value != null);
				assertEquals("Content type", expectedContentType, MediaType.parseMediaType(value));
			}
		};
	}
	
	/**
	 * Parse the {@code expectedContentType} and delegate to {@link #contentType(MediaType)}.
	 */
	public ResultMatcher contentType(final String expectedContentType) {
		return contentType(MediaType.valueOf(expectedContentType));
	}

	/**
	 * Match the expected character encoding to that of the ServletResponse.
	 */
	public ResultMatcher characterEncoding(final String expectedCharacterEncoding) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) {
				String value = response.getCharacterEncoding();
				assertEquals("Character encoding", expectedCharacterEncoding, value);
			}
		};
	}

	/**
	 * A {@code ServletResponseContentResultMatchers} for access to 
	 * ServletResponse content result matchers.
	 */
	public ContentResultMatchers content() {
		return this.contentMatchers;
	}
	
	/**
	 * Match the URL the response was forwarded to, to the {@code expectedUrl}.
	 */
	public ResultMatcher forwardedUrl(final String expectedUrl) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Forwarded URL", expectedUrl, response.getForwardedUrl());
			}
		};
	}

	/**
	 * Match the URL the response was redirected to, to the {@code expectedUrl}. 
	 */
	public ResultMatcher redirectedUrl(final String expectedUrl) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Redirected URL", expectedUrl, response.getRedirectedUrl());
			}
		};
	}

	/**
	 * Obtain a response header and match it to the {@code expectedValue}.
	 */
	public ResultMatcher header(final String headerName, final Object expectedValue) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Response header", expectedValue, response.getHeader(headerName));
			}
		};
	}

	/**
	 * Obtain the primary response header value and match it with the given {@link Matcher}.
	 * <p>Example:
	 * <pre>
	 * // import static org.hamcrest.Matchers.containsString;
	 * 
	 * mockMvc.perform(get("/path"))
	 *   .andExpect(response().header("someHeader", containsString("text")));
	 * </pre>
	 */
	public ResultMatcher header(final String headerName, final Matcher<String> matcher) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				MatcherAssert.assertThat("Response header", response.getHeader(headerName), matcher);
			}
		};
	}

	/**
	 * Obtain a response cookie value and match it to the {@code expectedValue}.
	 */
	public ResultMatcher cookieValue(final String cookieName, final String expectedValue) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				Cookie cookie = response.getCookie(cookieName);
				assertTrue("Cookie not found", cookie != null);
				assertEquals("Response cookie", expectedValue, cookie.getValue());
			}
		};
	}

	/**
	 * Obtain a response cookie value and match it with the given {@link Matcher}.
	 * <p>Example:
	 * <pre>
	 * // import static org.hamcrest.Matchers.containsString;
	 * 
	 * mockMvc.perform(get("/path"))
	 *   .andExpect(response().cookie("myCookie", containsString("text")));
	 * </pre>
	 */
	public ResultMatcher cookieValue(final String cookieName, final Matcher<String> matcher) {
		return new ServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				Cookie cookie = response.getCookie(cookieName);
				assertTrue("Cookie not found", cookie != null);
				MatcherAssert.assertThat("Response cookie", cookie.getValue(), matcher);
			}
		};
	}

	/**
	 * Base class for Matchers that assert the HttpServletResponse.
	 */
	public static abstract class ServletResponseResultMatcher implements ResultMatcher {

		public final void match(MockHttpServletRequest request, 
				MockHttpServletResponse response, 
				Object handler,	
				HandlerInterceptor[] interceptors, 
				ModelAndView mav, 
				Exception resolvedException) throws Exception {
			
			matchResponse(response);
		}

		protected abstract void matchResponse(MockHttpServletResponse response) throws Exception;
	}
	
}
