package org.springframework.test.web.server.result;

import static org.springframework.test.web.AssertionErrors.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;

/**
 * Provides methods to define expectations on the status of the response.
 * 
 * @author Keesun Baik
 * @author Rossen Stoyanchev
 */
public class StatusResultMatchers {

	/**
	 * Assert the response status code with the given matcher.
	 * @see #reason(Matcher)
	 * @see #reason(String)
	 */
	public ResultMatcher is(final Matcher<Integer> matcher) {
		return new ResultMatcherAdapter() {
			
			@Override
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				MatcherAssert.assertThat("Status: ", response.getStatus(), matcher);
			}
		};
	}

	/**
	 * Assert the response status code is equal to an integer value.
	 * @see #reason(Matcher)
	 * @see #reason(String)
	 */
	public ResultMatcher is(int status) {
		return is(Matchers.equalTo(status));
	}


	/**
	 * Assert the response reason with the given matcher.
	 * @see HttpServletResponse#sendError(int, String)
	 */
	public ResultMatcher reason(final Matcher<? super String> matcher) {
		return new ResultMatcherAdapter() {
			
			@Override
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				MatcherAssert.assertThat("Status reason: ", response.getErrorMessage(), matcher);
			}
		};
	}
	
	/**
	 * Assert the response reason is equal to a String value.
	 * @see HttpServletResponse#sendError(int, String)
	 */
	public ResultMatcher reason(String reason) {
		return reason(Matchers.equalTo(reason));
	}

    /**
     * Assert the response status is {@code HttpStatus.CONTINUE} (100)
     */
    public ResultMatcher isContinue(){
        return matcher(HttpStatus.CONTINUE);
    }

    /**
     * Assert the response status is {@code HttpStatus.SWITCHING_PROTOCOLS} (101)
     */
    public ResultMatcher isSwitchingProtocols(){
        return matcher(HttpStatus.SWITCHING_PROTOCOLS);
    }

    /**
     * Assert the response status is {@code HttpStatus.PROCESSING} (102)
     */
    public ResultMatcher isProcessing(){
        return matcher(HttpStatus.PROCESSING);
    }

    /**
     * Assert the response status is {@code HttpStatus.OK} (200)
     */
    public ResultMatcher isOk(){
        return matcher(HttpStatus.OK);
    }

    /**
     * Assert the response status is {@code HttpStatus.CREATED} (201)
     */
    public ResultMatcher isCreated(){
        return matcher(HttpStatus.CREATED);
    }

    /**
     * Assert the response status is {@code HttpStatus.ACCEPTED} (202)
     */
    public ResultMatcher isAccepted(){
        return matcher(HttpStatus.ACCEPTED);
    }

    /**
     * Assert the response status is {@code HttpStatus.NON_AUTHORITATIVE_INFORMATION} (203)
     */
    public ResultMatcher isNonAuthoritativeInformation(){
        return matcher(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    /**
     * Assert the response status is {@code HttpStatus.NO_CONTENT} (204)
     */
    public ResultMatcher isNoContent(){
        return matcher(HttpStatus.NO_CONTENT);
    }

    /**
     * Assert the response status is {@code HttpStatus.RESET_CONTENT} (205)
     */
    public ResultMatcher isResetContent(){
        return matcher(HttpStatus.RESET_CONTENT);
    }

    /**
     * Assert the response status is {@code HttpStatus.PARTIAL_CONTENT} (206)
     */
    public ResultMatcher isPartialContent(){
        return matcher(HttpStatus.PARTIAL_CONTENT);
    }

    /**
     * Assert the response status is {@code HttpStatus.MULTI_STATUS} (207)
     */
    public ResultMatcher isMultiStatus(){
        return matcher(HttpStatus.MULTI_STATUS);
    }

    /**
     * Assert the response status is {@code HttpStatus.ALREADY_REPORTED} (208)
     */
    public ResultMatcher isAlreadyReported(){
        return matcher(HttpStatus.ALREADY_REPORTED);
    }

    /**
     * Assert the response status is {@code HttpStatus.IM_USED} (226)
     */
    public ResultMatcher isImUsed(){
        return matcher(HttpStatus.IM_USED);
    }

    /**
     * Assert the response status is {@code HttpStatus.MULTIPLE_CHOICES} (300)
     */
    public ResultMatcher isMultipleChoices(){
        return matcher(HttpStatus.MULTIPLE_CHOICES);
    }

    /**
     * Assert the response status is {@code HttpStatus.MOVED_PERMANENTLY} (301)
     */
    public ResultMatcher isMovedPermanently(){
        return matcher(HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Assert the response status is {@code HttpStatus.FOUND} (302)
     */
    public ResultMatcher isFound(){
        return matcher(HttpStatus.FOUND);
    }

    /**
     * Assert the response status is {@code HttpStatus.MOVED_TEMPORARILY} (302) 
     */
    public ResultMatcher isMovedTemporarily(){
        return matcher(HttpStatus.MOVED_TEMPORARILY);
    }

    /**
     * Assert the response status is {@code HttpStatus.SEE_OTHER} (303)
     */
    public ResultMatcher isSeeOther(){
        return matcher(HttpStatus.SEE_OTHER);
    }

    /**
     * Assert the response status is {@code HttpStatus.NOT_MODIFIED} (304)
     */
    public ResultMatcher isNotModified(){
        return matcher(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Assert the response status is {@code HttpStatus.USE_PROXY} (305)
     */
    public ResultMatcher isUseProxy(){
        return matcher(HttpStatus.USE_PROXY);
    }

    /**
     * Assert the response status is {@code HttpStatus.TEMPORARY_REDIRECT} (307)
     */
    public ResultMatcher isTemporaryRedirect(){
        return matcher(HttpStatus.TEMPORARY_REDIRECT);
    }

    /**
     * Assert the response status is {@code HttpStatus.BAD_REQUEST} (400)
     */
    public ResultMatcher isBadRequest(){
        return matcher(HttpStatus.BAD_REQUEST);
    }

    /**
     * Assert the response status is {@code HttpStatus.UNAUTHORIZED} (401)
     */
    public ResultMatcher isUnauthorized(){
        return matcher(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Assert the response status is {@code HttpStatus.PAYMENT_REQUIRED} (402)
     */
    public ResultMatcher isPaymentRequired(){
        return matcher(HttpStatus.PAYMENT_REQUIRED);
    }

    /**
     * Assert the response status is {@code HttpStatus.FORBIDDEN} (403)
     */
    public ResultMatcher isForbidden(){
        return matcher(HttpStatus.FORBIDDEN);
    }

	/**
     * Assert the response status is {@code HttpStatus.NOT_FOUND} (404)
     */
    public ResultMatcher isNotFound(){
        return matcher(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Assert the response status is {@code HttpStatus.METHOD_NOT_ALLOWED} (405)
     */
    public ResultMatcher isMethodNotAllowed(){
        return matcher(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Assert the response status is {@code HttpStatus.NOT_ACCEPTABLE} (406)
     */
    public ResultMatcher isNotAcceptable(){
        return matcher(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Assert the response status is {@code HttpStatus.PROXY_AUTHENTICATION_REQUIRED} (407)
     */
    public ResultMatcher isProxyAuthenticationRequired(){
        return matcher(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    /**
     * Assert the response status is {@code HttpStatus.REQUEST_TIMEOUT} (408)
     */
    public ResultMatcher isRequestTimeout(){
        return matcher(HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Assert the response status is {@code HttpStatus.CONFLICT} (409)
     */
    public ResultMatcher isConflict(){
        return matcher(HttpStatus.CONFLICT);
    }

    /**
     * Assert the response status is {@code HttpStatus.GONE} (410)
     */
    public ResultMatcher isGone(){
        return matcher(HttpStatus.GONE);
    }

    /**
     * Assert the response status is {@code HttpStatus.LENGTH_REQUIRED} (411)
     */
    public ResultMatcher isLengthRequired(){
        return matcher(HttpStatus.LENGTH_REQUIRED);
    }

    /**
     * Assert the response status is {@code HttpStatus.PRECONDITION_FAILED} (412)
     */
    public ResultMatcher isPreconditionFailed(){
        return matcher(HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * Assert the response status is {@code HttpStatus.REQUEST_ENTITY_TOO_LARGE} (413)
     */
    public ResultMatcher isRequestEntityTooLarge(){
        return matcher(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
    }

    /**
     * Assert the response status is {@code HttpStatus.REQUEST_URI_TOO_LONG} (414)
     */
    public ResultMatcher isRequestUriTooLong(){
        return matcher(HttpStatus.REQUEST_URI_TOO_LONG);
    }

    /**
     * Assert the response status is {@code HttpStatus.UNSUPPORTED_MEDIA_TYPE} (415)
     */
    public ResultMatcher isUnsupportedMediaType(){
        return matcher(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Assert the response status is {@code HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE} (416)
     */
    public ResultMatcher isRequestedRangeNotSatisfiable(){
        return matcher(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * Assert the response status is {@code HttpStatus.EXPECTATION_FAILED} (417)
     * Check if the HTTP is code is 417 or not.
     * @return true if the is code is 417.
     */
    public ResultMatcher isExpectationFailed(){
        return matcher(HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * Assert the response status is {@code HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE} (419)
     */
    public ResultMatcher isInsufficientSpaceOnResource(){
        return matcher(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
    }

    /**
     * Assert the response status is {@code HttpStatus.METHOD_FAILURE} (420)
     */
    public ResultMatcher isMethodFailure(){
        return matcher(HttpStatus.METHOD_FAILURE);
    }

    /**
     * Assert the response status is {@code HttpStatus.DESTINATION_LOCKED} (421)
     */
    public ResultMatcher isDestinationLocked(){
        return matcher(HttpStatus.DESTINATION_LOCKED);
    }

    /**
     * Assert the response status is {@code HttpStatus.UNPROCESSABLE_ENTITY} (422)
     */
    public ResultMatcher isUnprocessableEntity(){
        return matcher(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Assert the response status is {@code HttpStatus.LOCKED} (423)
     */
    public ResultMatcher isLocked(){
        return matcher(HttpStatus.LOCKED);
    }

    /**
     * Assert the response status is {@code HttpStatus.FAILED_DEPENDENCY} (424)
     */
    public ResultMatcher isFailedDependency(){
        return matcher(HttpStatus.FAILED_DEPENDENCY);
    }

    /**
     * Assert the response status is {@code HttpStatus.UPGRADE_REQUIRED} (426)
     */
    public ResultMatcher isUpgradeRequired(){
        return matcher(HttpStatus.UPGRADE_REQUIRED);
    }

    /**
     * Assert the response status is {@code HttpStatus.INTERNAL_SERVER_ERROR} (500)
     */
    public ResultMatcher isInternalServerError(){
        return matcher(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Assert the response status is {@code HttpStatus.NOT_IMPLEMENTED} (501)
     */
    public ResultMatcher isNotImplemented(){
        return matcher(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Assert the response status is {@code HttpStatus.BAD_GATEWAY} (502)
     */
    public ResultMatcher isBadGateway(){
        return matcher(HttpStatus.BAD_GATEWAY);
    }

    /**
     * Assert the response status is {@code HttpStatus.SERVICE_UNAVAILABLE} (503)
     */
    public ResultMatcher isServiceUnavailable(){
        return matcher(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Assert the response status is {@code HttpStatus.GATEWAY_TIMEOUT} (504)
     */
    public ResultMatcher isGatewayTimeout(){
        return matcher(HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Assert the response status is {@code HttpStatus.HTTP_VERSION_NOT_SUPPORTED} (505)
     */
    public ResultMatcher isHttpVersionNotSupported(){
        return matcher(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }

    /**
     * Assert the response status is {@code HttpStatus.VARIANT_ALSO_NEGOTIATES} (506)
     */
    public ResultMatcher isVariantAlsoNegotiates(){
        return matcher(HttpStatus.VARIANT_ALSO_NEGOTIATES);
    }

    /**
     * Assert the response status is {@code HttpStatus.INSUFFICIENT_STORAGE} (507)
     */
    public ResultMatcher isInsufficientStorage(){
        return matcher(HttpStatus.INSUFFICIENT_STORAGE);
    }

    /**
     * Assert the response status is {@code HttpStatus.LOOP_DETECTED} (508)
     */
    public ResultMatcher isLoopDetected(){
        return matcher(HttpStatus.LOOP_DETECTED);
    }

    /**
     * Assert the response status is {@code HttpStatus.NOT_EXTENDED} (509)
     */
    public ResultMatcher isNotExtended(){
        return matcher(HttpStatus.NOT_EXTENDED);
    }

    /**
	 * Match the expected response status to that of the HttpServletResponse
	 */
    private ResultMatcher matcher(final HttpStatus status) {
		return new ResultMatcherAdapter() {
			
			@Override
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", status.value(), response.getStatus());
			}
		};
	}
	
}
