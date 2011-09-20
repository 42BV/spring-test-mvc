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

	public ResultMatcher status(final HttpStatus status) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", status, HttpStatus.valueOf(response.getStatus()));
			}
		};
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

	public static abstract class MockHttpServletResponseResultMatcher implements ResultMatcher {

		public final void match(MockHttpServletRequest request, 
				MockHttpServletResponse response, 
				Object handler,	
				HandlerInterceptor[] interceptors, 
				ModelAndView mav, 
				Exception resolvedException) {
			
			try {
				matchResponse(response);
			}
			catch (IOException e) {
				e.printStackTrace();
				AssertionErrors.fail("Failed mock response expectation: " + e.getMessage());
			}
		}

		protected abstract void matchResponse(MockHttpServletResponse response) throws IOException;
	}


    /**
     * Convenience Methods for HttpStatus check
     */

    /**
     * Convenience Method for {@link HttpStatus.OK}
     * Check if the http status code is 200 or not.
     * @return true if the status code is 200.
     */
    public ResultMatcher isOk(){
        return status(HttpStatus.OK);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_FOUND}
     * Check if the http status code is 404 or not.
     * @return true if the status code is 404.
     */
    public ResultMatcher isNotFound(){
        return status(HttpStatus.NOT_FOUND);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONTINUE}
     * Check if the http status code is 100 or not.
     * @return true if the status code is 100.
     */
    public ResultMatcher isContinue(){
        return status(HttpStatus.CONTINUE);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONTINUE}
     * Check if the http status code is 101 or not.
     * @return true if the status code is 101.
     */
    public ResultMatcher isSwitchingProtocols(){
        return status(HttpStatus.SWITCHING_PROTOCOLS);
    }

    /**
     * Convenience Method for {@link HttpStatus.PROCESSING}
     * Check if the http status code is 102 or not.
     * @return true if the status code is 102.
     */
    public ResultMatcher isProcessing(){
        return status(HttpStatus.PROCESSING);
    }

    /**
     * Convenience Method for {@link HttpStatus.CREATED}
     * Check if the http status code is 201 or not.
     * @return true if the status code is 201.
     */
    public ResultMatcher isCreated(){
        return status(HttpStatus.CREATED);
    }

    /**
     * Convenience Method for {@link HttpStatus.ACCEPTED}
     * Check if the http status code is 202 or not.
     * @return true if the status code is 202.
     */
    public ResultMatcher isAccepted(){
        return status(HttpStatus.ACCEPTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NON_AUTHORITATIVE_INFORMATION}
     * Check if the http status code is 203 or not.
     * @return true if the status code is 203.
     */
    public ResultMatcher isNonAuthoritativeInformation(){
        return status(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }


    /**
     * Convenience Method for {@link HttpStatus.NO_CONTENT}
     * Check if the http status code is 204 or not.
     * @return true if the status code is 204.
     */
    public ResultMatcher isNoContent(){
        return status(HttpStatus.NO_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.RESET_CONTENT}
     * Check if the http status code is 205 or not.
     * @return true if the status code is 205.
     */
    public ResultMatcher isResetContent(){
        return status(HttpStatus.RESET_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.PARTIAL_CONTENT}
     * Check if the http status code is 206 or not.
     * @return true if the status code is 206.
     */
    public ResultMatcher isPartialContent(){
        return status(HttpStatus.PARTIAL_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.MULTI_STATUS}
     * Check if the http status code is 207 or not.
     * @return true if the status code is 207.
     */
    public ResultMatcher isMultiStatus(){
        return status(HttpStatus.MULTI_STATUS);
    }

    /**
     * Convenience Method for {@link HttpStatus.ALREADY_REPORTED}
     * Check if the http status code is 208 or not.
     * @return true if the status code is 208.
     */
    public ResultMatcher isAlreadyReported(){
        return status(HttpStatus.ALREADY_REPORTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.IM_USED}
     * Check if the http status code is 226 or not.
     * @return true if the status code is 226.
     */
    public ResultMatcher isImUsed(){
        return status(HttpStatus.IM_USED);
    }

    /**
     * Convenience Method for {@link HttpStatus.MULTIPLE_CHOICES}
     * Check if the http status code is 300 or not.
     * @return true if the status code is 300.
     */
    public ResultMatcher isMultipleChoices(){
        return status(HttpStatus.MULTIPLE_CHOICES);
    }

    /**
     * Convenience Method for {@link HttpStatus.MOVED_PERMANENTLY}
     * Check if the http status code is 301 or not.
     * @return true if the status code is 301.
     */
    public ResultMatcher isMovedPermanently(){
        return status(HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Convenience Method for {@link HttpStatus.FOUND}
     * Check if the http status code is 302 or not.
     * @return true if the status code is 302.
     */
    public ResultMatcher isFound(){
        return status(HttpStatus.FOUND);
    }

    /**
     * Convenience Method for {@link HttpStatus.MOVED_TEMPORARILY}
     * Check if the http status code is 302 or not.
     * @return true if the status code is 302.
     */
    public ResultMatcher isMovedTemporarily(){
        return status(HttpStatus.MOVED_TEMPORARILY);
    }

    /**
     * Convenience Method for {@link HttpStatus.SEE_OTHER}
     * Check if the http status code is 303 or not.
     * @return true if the status code is 303.
     */
    public ResultMatcher isSeeOther(){
        return status(HttpStatus.SEE_OTHER);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_MODIFIED}
     * Check if the http status code is 304 or not.
     * @return true if the status code is 304.
     */
    public ResultMatcher isNotModified(){
        return status(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Convenience Method for {@link HttpStatus.USE_PROXY}
     * Check if the http status code is 305 or not.
     * @return true if the status code is 305.
     */
    public ResultMatcher isUseProxy(){
        return status(HttpStatus.USE_PROXY);
    }

    /**
     * Convenience Method for {@link HttpStatus.TEMPORARY_REDIRECT}
     * Check if the http status code is 307 or not.
     * @return true if the status code is 307.
     */
    public ResultMatcher isTemporaryRedirect(){
        return status(HttpStatus.TEMPORARY_REDIRECT);
    }

    /**
     * Convenience Method for {@link HttpStatus.BAD_REQUEST}
     * Check if the http status code is 400 or not.
     * @return true if the status code is 400.
     */
    public ResultMatcher isBadRequest(){
        return status(HttpStatus.BAD_REQUEST);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNAUTHORIZED}
     * Check if the http status code is 401 or not.
     * @return true if the status code is 401.
     */
    public ResultMatcher isUnauthorized(){
        return status(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Convenience Method for {@link HttpStatus.PAYMENT_REQUIRED}
     * Check if the http status code is 402 or not.
     * @return true if the status code is 402.
     */
    public ResultMatcher isPaymentRequired(){
        return status(HttpStatus.PAYMENT_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.FORBIDDEN}
     * Check if the http status code is 403 or not.
     * @return true if the status code is 403.
     */
    public ResultMatcher isForbidden(){
        return status(HttpStatus.FORBIDDEN);
    }

    /**
     * Convenience Method for {@link HttpStatus.METHOD_NOT_ALLOWED}
     * Check if the http status code is 405 or not.
     * @return true if the status code is 405.
     */
    public ResultMatcher isMethodNotAllowed(){
        return status(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_ACCEPTABLE}
     * Check if the http status code is 406 or not.
     * @return true if the status code is 406.
     */
    public ResultMatcher isNotAcceptable(){
        return status(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.PROXY_AUTHENTICATION_REQUIRED}
     * Check if the http status code is 407 or not.
     * @return true if the status code is 407.
     */
    public ResultMatcher isProxyAuthenticationRequired(){
        return status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_TIMEOUT}
     * Check if the http status code is 408 or not.
     * @return true if the status code is 408.
     */
    public ResultMatcher isRequestTimeout(){
        return status(HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONFLICT}
     * Check if the http status code is 409 or not.
     * @return true if the status code is 409.
     */
    public ResultMatcher isConflict(){
        return status(HttpStatus.CONFLICT);
    }

    /**
     * Convenience Method for {@link HttpStatus.GONE}
     * Check if the http status code is 410 or not.
     * @return true if the status code is 410.
     */
    public ResultMatcher isGone(){
        return status(HttpStatus.GONE);
    }

    /**
     * Convenience Method for {@link HttpStatus.LENGTH_REQUIRED}
     * Check if the http status code is 411 or not.
     * @return true if the status code is 411.
     */
    public ResultMatcher isLengthRequired(){
        return status(HttpStatus.LENGTH_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.PRECONDITION_FAILED}
     * Check if the http status code is 412 or not.
     * @return true if the status code is 412.
     */
    public ResultMatcher isPreconditionFailed(){
        return status(HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_ENTITY_TOO_LARGE}
     * Check if the http status code is 413 or not.
     * @return true if the status code is 413.
     */
    public ResultMatcher isRequestEntityTooLarge(){
        return status(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_URI_TOO_LONG}
     * Check if the http status code is 414 or not.
     * @return true if the status code is 414.
     */
    public ResultMatcher isRequestUriTooLong(){
        return status(HttpStatus.REQUEST_URI_TOO_LONG);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNSUPPORTED_MEDIA_TYPE}
     * Check if the http status code is 415 or not.
     * @return true if the status code is 415.
     */
    public ResultMatcher isUnsupportedMediaType(){
        return status(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE}
     * Check if the http status code is 416 or not.
     * @return true if the status code is 416.
     */
    public ResultMatcher isRequestedRangeNotSatisfiable(){
        return status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.EXPECTATION_FAILED}
     * Check if the http status code is 417 or not.
     * @return true if the status code is 417.
     */
    public ResultMatcher isExpectationFailed(){
        return status(HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * Convenience Method for {@link HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE}
     * Check if the http status code is 419 or not.
     * @return true if the status code is 419.
     */
    public ResultMatcher isInsufficientSpaceOnResource(){
        return status(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
    }

    /**
     * Convenience Method for {@link HttpStatus.METHOD_FAILURE}
     * Check if the http status code is 420 or not.
     * @return true if the status code is 420.
     */
    public ResultMatcher isMethodFailure(){
        return status(HttpStatus.METHOD_FAILURE);
    }

    /**
     * Convenience Method for {@link HttpStatus.DESTINATION_LOCKED}
     * Check if the http status code is 421 or not.
     * @return true if the status code is 421.
     */
    public ResultMatcher isDestinationLocked(){
        return status(HttpStatus.DESTINATION_LOCKED);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNPROCESSABLE_ENTITY}
     * Check if the http status code is 422 or not.
     * @return true if the status code is 422.
     */
    public ResultMatcher isUnprocessableEntity(){
        return status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Convenience Method for {@link HttpStatus.LOCKED}
     * Check if the http status code is 423 or not.
     * @return true if the status code is 423.
     */
    public ResultMatcher isLocked(){
        return status(HttpStatus.LOCKED);
    }

    /**
     * Convenience Method for {@link HttpStatus.FAILED_DEPENDENCY}
     * Check if the http status code is 424 or not.
     * @return true if the status code is 424.
     */
    public ResultMatcher isFailedDependency(){
        return status(HttpStatus.FAILED_DEPENDENCY);
    }

    /**
     * Convenience Method for {@link HttpStatus.UPGRADE_REQUIRED}
     * Check if the http status code is 426 or not.
     * @return true if the status code is 426.
     */
    public ResultMatcher isUpgradeRequired(){
        return status(HttpStatus.UPGRADE_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.INTERNAL_SERVER_ERROR}
     * Check if the http status code is 500 or not.
     * @return true if the status code is 500.
     */
    public ResultMatcher isInternalServerError(){
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_IMPLEMENTED}
     * Check if the http status code is 501 or not.
     * @return true if the status code is 501.
     */
    public ResultMatcher isNotImplemented(){
        return status(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.BAD_GATEWAY}
     * Check if the http status code is 502 or not.
     * @return true if the status code is 502.
     */
    public ResultMatcher isBadGateway(){
        return status(HttpStatus.BAD_GATEWAY);
    }

    /**
     * Convenience Method for {@link HttpStatus.SERVICE_UNAVAILABLE}
     * Check if the http status code is 503 or not.
     * @return true if the status code is 503.
     */
    public ResultMatcher isServiceUnavailable(){
        return status(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.GATEWAY_TIMEOUT}
     * Check if the http status code is 504 or not.
     * @return true if the status code is 504.
     */
    public ResultMatcher isGatewayTimeout(){
        return status(HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Convenience Method for {@link HttpStatus.HTTP_VERSION_NOT_SUPPORTED}
     * Check if the http status code is 505 or not.
     * @return true if the status code is 505.
     */
    public ResultMatcher isHttpVersionNotSupported(){
        return status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.VARIANT_ALSO_NEGOTIATES}
     * Check if the http status code is 506 or not.
     * @return true if the status code is 506.
     */
    public ResultMatcher isVariantAlsoNegotiates(){
        return status(HttpStatus.VARIANT_ALSO_NEGOTIATES);
    }

    /**
     * Convenience Method for {@link HttpStatus.INSUFFICIENT_STORAGE}
     * Check if the http status code is 507 or not.
     * @return true if the status code is 507.
     */
    public ResultMatcher isInsufficientStorage(){
        return status(HttpStatus.INSUFFICIENT_STORAGE);
    }

    /**
     * Convenience Method for {@link HttpStatus.LOOP_DETECTED}
     * Check if the http status code is 508 or not.
     * @return true if the status code is 508.
     */
    public ResultMatcher isLoopDetected(){
        return status(HttpStatus.LOOP_DETECTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_EXTENDED}
     * Check if the http status code is 509 or not.
     * @return true if the status code is 509.
     */
    public ResultMatcher isNotExtended(){
        return status(HttpStatus.NOT_EXTENDED);
    }
}