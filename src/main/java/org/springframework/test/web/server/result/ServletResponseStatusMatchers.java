package org.springframework.test.web.server.result;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;

import static org.springframework.test.web.AssertionErrors.assertEquals;

public class ServletResponseStatusMatchers {

    public ResultMatcher is(final HttpStatus status) {
		return new MockHttpServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", status, HttpStatus.valueOf(response.getStatus()));
			}
		};
	}

    /**
     * Convenience Methods for HttpStatus check
     */

    /**
     * Convenience Method for {@link HttpStatus.OK}
     * Check if the http is code is 200 or not.
     * @return true if the is code is 200.
     */
    public ResultMatcher isOk(){
        return is(HttpStatus.OK);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_FOUND}
     * Check if the http is code is 404 or not.
     * @return true if the is code is 404.
     */
    public ResultMatcher isNotFound(){
        return is(HttpStatus.NOT_FOUND);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONTINUE}
     * Check if the http is code is 100 or not.
     * @return true if the is code is 100.
     */
    public ResultMatcher isContinue(){
        return is(HttpStatus.CONTINUE);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONTINUE}
     * Check if the http is code is 101 or not.
     * @return true if the is code is 101.
     */
    public ResultMatcher isSwitchingProtocols(){
        return is(HttpStatus.SWITCHING_PROTOCOLS);
    }

    /**
     * Convenience Method for {@link HttpStatus.PROCESSING}
     * Check if the http is code is 102 or not.
     * @return true if the is code is 102.
     */
    public ResultMatcher isProcessing(){
        return is(HttpStatus.PROCESSING);
    }

    /**
     * Convenience Method for {@link HttpStatus.CREATED}
     * Check if the http is code is 201 or not.
     * @return true if the is code is 201.
     */
    public ResultMatcher isCreated(){
        return is(HttpStatus.CREATED);
    }

    /**
     * Convenience Method for {@link HttpStatus.ACCEPTED}
     * Check if the http is code is 202 or not.
     * @return true if the is code is 202.
     */
    public ResultMatcher isAccepted(){
        return is(HttpStatus.ACCEPTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NON_AUTHORITATIVE_INFORMATION}
     * Check if the http is code is 203 or not.
     * @return true if the is code is 203.
     */
    public ResultMatcher isNonAuthoritativeInformation(){
        return is(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }


    /**
     * Convenience Method for {@link HttpStatus.NO_CONTENT}
     * Check if the http is code is 204 or not.
     * @return true if the is code is 204.
     */
    public ResultMatcher isNoContent(){
        return is(HttpStatus.NO_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.RESET_CONTENT}
     * Check if the http is code is 205 or not.
     * @return true if the is code is 205.
     */
    public ResultMatcher isResetContent(){
        return is(HttpStatus.RESET_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.PARTIAL_CONTENT}
     * Check if the http is code is 206 or not.
     * @return true if the is code is 206.
     */
    public ResultMatcher isPartialContent(){
        return is(HttpStatus.PARTIAL_CONTENT);
    }

    /**
     * Convenience Method for {@link HttpStatus.MULTI_STATUS}
     * Check if the http is code is 207 or not.
     * @return true if the is code is 207.
     */
    public ResultMatcher isMultiStatus(){
        return is(HttpStatus.MULTI_STATUS);
    }

    /**
     * Convenience Method for {@link HttpStatus.ALREADY_REPORTED}
     * Check if the http is code is 208 or not.
     * @return true if the is code is 208.
     */
    public ResultMatcher isAlreadyReported(){
        return is(HttpStatus.ALREADY_REPORTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.IM_USED}
     * Check if the http is code is 226 or not.
     * @return true if the is code is 226.
     */
    public ResultMatcher isImUsed(){
        return is(HttpStatus.IM_USED);
    }

    /**
     * Convenience Method for {@link HttpStatus.MULTIPLE_CHOICES}
     * Check if the http is code is 300 or not.
     * @return true if the is code is 300.
     */
    public ResultMatcher isMultipleChoices(){
        return is(HttpStatus.MULTIPLE_CHOICES);
    }

    /**
     * Convenience Method for {@link HttpStatus.MOVED_PERMANENTLY}
     * Check if the http is code is 301 or not.
     * @return true if the is code is 301.
     */
    public ResultMatcher isMovedPermanently(){
        return is(HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Convenience Method for {@link HttpStatus.FOUND}
     * Check if the http is code is 302 or not.
     * @return true if the is code is 302.
     */
    public ResultMatcher isFound(){
        return is(HttpStatus.FOUND);
    }

    /**
     * Convenience Method for {@link HttpStatus.MOVED_TEMPORARILY}
     * Check if the http is code is 302 or not.
     * @return true if the is code is 302.
     */
    public ResultMatcher isMovedTemporarily(){
        return is(HttpStatus.MOVED_TEMPORARILY);
    }

    /**
     * Convenience Method for {@link HttpStatus.SEE_OTHER}
     * Check if the http is code is 303 or not.
     * @return true if the is code is 303.
     */
    public ResultMatcher isSeeOther(){
        return is(HttpStatus.SEE_OTHER);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_MODIFIED}
     * Check if the http is code is 304 or not.
     * @return true if the is code is 304.
     */
    public ResultMatcher isNotModified(){
        return is(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Convenience Method for {@link HttpStatus.USE_PROXY}
     * Check if the http is code is 305 or not.
     * @return true if the is code is 305.
     */
    public ResultMatcher isUseProxy(){
        return is(HttpStatus.USE_PROXY);
    }

    /**
     * Convenience Method for {@link HttpStatus.TEMPORARY_REDIRECT}
     * Check if the http is code is 307 or not.
     * @return true if the is code is 307.
     */
    public ResultMatcher isTemporaryRedirect(){
        return is(HttpStatus.TEMPORARY_REDIRECT);
    }

    /**
     * Convenience Method for {@link HttpStatus.BAD_REQUEST}
     * Check if the http is code is 400 or not.
     * @return true if the is code is 400.
     */
    public ResultMatcher isBadRequest(){
        return is(HttpStatus.BAD_REQUEST);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNAUTHORIZED}
     * Check if the http is code is 401 or not.
     * @return true if the is code is 401.
     */
    public ResultMatcher isUnauthorized(){
        return is(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Convenience Method for {@link HttpStatus.PAYMENT_REQUIRED}
     * Check if the http is code is 402 or not.
     * @return true if the is code is 402.
     */
    public ResultMatcher isPaymentRequired(){
        return is(HttpStatus.PAYMENT_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.FORBIDDEN}
     * Check if the http is code is 403 or not.
     * @return true if the is code is 403.
     */
    public ResultMatcher isForbidden(){
        return is(HttpStatus.FORBIDDEN);
    }

    /**
     * Convenience Method for {@link HttpStatus.METHOD_NOT_ALLOWED}
     * Check if the http is code is 405 or not.
     * @return true if the is code is 405.
     */
    public ResultMatcher isMethodNotAllowed(){
        return is(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_ACCEPTABLE}
     * Check if the http is code is 406 or not.
     * @return true if the is code is 406.
     */
    public ResultMatcher isNotAcceptable(){
        return is(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.PROXY_AUTHENTICATION_REQUIRED}
     * Check if the http is code is 407 or not.
     * @return true if the is code is 407.
     */
    public ResultMatcher isProxyAuthenticationRequired(){
        return is(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_TIMEOUT}
     * Check if the http is code is 408 or not.
     * @return true if the is code is 408.
     */
    public ResultMatcher isRequestTimeout(){
        return is(HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Convenience Method for {@link HttpStatus.CONFLICT}
     * Check if the http is code is 409 or not.
     * @return true if the is code is 409.
     */
    public ResultMatcher isConflict(){
        return is(HttpStatus.CONFLICT);
    }

    /**
     * Convenience Method for {@link HttpStatus.GONE}
     * Check if the http is code is 410 or not.
     * @return true if the is code is 410.
     */
    public ResultMatcher isGone(){
        return is(HttpStatus.GONE);
    }

    /**
     * Convenience Method for {@link HttpStatus.LENGTH_REQUIRED}
     * Check if the http is code is 411 or not.
     * @return true if the is code is 411.
     */
    public ResultMatcher isLengthRequired(){
        return is(HttpStatus.LENGTH_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.PRECONDITION_FAILED}
     * Check if the http is code is 412 or not.
     * @return true if the is code is 412.
     */
    public ResultMatcher isPreconditionFailed(){
        return is(HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_ENTITY_TOO_LARGE}
     * Check if the http is code is 413 or not.
     * @return true if the is code is 413.
     */
    public ResultMatcher isRequestEntityTooLarge(){
        return is(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUEST_URI_TOO_LONG}
     * Check if the http is code is 414 or not.
     * @return true if the is code is 414.
     */
    public ResultMatcher isRequestUriTooLong(){
        return is(HttpStatus.REQUEST_URI_TOO_LONG);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNSUPPORTED_MEDIA_TYPE}
     * Check if the http is code is 415 or not.
     * @return true if the is code is 415.
     */
    public ResultMatcher isUnsupportedMediaType(){
        return is(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Convenience Method for {@link HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE}
     * Check if the http is code is 416 or not.
     * @return true if the is code is 416.
     */
    public ResultMatcher isRequestedRangeNotSatisfiable(){
        return is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.EXPECTATION_FAILED}
     * Check if the http is code is 417 or not.
     * @return true if the is code is 417.
     */
    public ResultMatcher isExpectationFailed(){
        return is(HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * Convenience Method for {@link HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE}
     * Check if the http is code is 419 or not.
     * @return true if the is code is 419.
     */
    public ResultMatcher isInsufficientSpaceOnResource(){
        return is(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
    }

    /**
     * Convenience Method for {@link HttpStatus.METHOD_FAILURE}
     * Check if the http is code is 420 or not.
     * @return true if the is code is 420.
     */
    public ResultMatcher isMethodFailure(){
        return is(HttpStatus.METHOD_FAILURE);
    }

    /**
     * Convenience Method for {@link HttpStatus.DESTINATION_LOCKED}
     * Check if the http is code is 421 or not.
     * @return true if the is code is 421.
     */
    public ResultMatcher isDestinationLocked(){
        return is(HttpStatus.DESTINATION_LOCKED);
    }

    /**
     * Convenience Method for {@link HttpStatus.UNPROCESSABLE_ENTITY}
     * Check if the http is code is 422 or not.
     * @return true if the is code is 422.
     */
    public ResultMatcher isUnprocessableEntity(){
        return is(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Convenience Method for {@link HttpStatus.LOCKED}
     * Check if the http is code is 423 or not.
     * @return true if the is code is 423.
     */
    public ResultMatcher isLocked(){
        return is(HttpStatus.LOCKED);
    }

    /**
     * Convenience Method for {@link HttpStatus.FAILED_DEPENDENCY}
     * Check if the http is code is 424 or not.
     * @return true if the is code is 424.
     */
    public ResultMatcher isFailedDependency(){
        return is(HttpStatus.FAILED_DEPENDENCY);
    }

    /**
     * Convenience Method for {@link HttpStatus.UPGRADE_REQUIRED}
     * Check if the http is code is 426 or not.
     * @return true if the is code is 426.
     */
    public ResultMatcher isUpgradeRequired(){
        return is(HttpStatus.UPGRADE_REQUIRED);
    }

    /**
     * Convenience Method for {@link HttpStatus.INTERNAL_SERVER_ERROR}
     * Check if the http is code is 500 or not.
     * @return true if the is code is 500.
     */
    public ResultMatcher isInternalServerError(){
        return is(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_IMPLEMENTED}
     * Check if the http is code is 501 or not.
     * @return true if the is code is 501.
     */
    public ResultMatcher isNotImplemented(){
        return is(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.BAD_GATEWAY}
     * Check if the http is code is 502 or not.
     * @return true if the is code is 502.
     */
    public ResultMatcher isBadGateway(){
        return is(HttpStatus.BAD_GATEWAY);
    }

    /**
     * Convenience Method for {@link HttpStatus.SERVICE_UNAVAILABLE}
     * Check if the http is code is 503 or not.
     * @return true if the is code is 503.
     */
    public ResultMatcher isServiceUnavailable(){
        return is(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Convenience Method for {@link HttpStatus.GATEWAY_TIMEOUT}
     * Check if the http is code is 504 or not.
     * @return true if the is code is 504.
     */
    public ResultMatcher isGatewayTimeout(){
        return is(HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Convenience Method for {@link HttpStatus.HTTP_VERSION_NOT_SUPPORTED}
     * Check if the http is code is 505 or not.
     * @return true if the is code is 505.
     */
    public ResultMatcher isHttpVersionNotSupported(){
        return is(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.VARIANT_ALSO_NEGOTIATES}
     * Check if the http is code is 506 or not.
     * @return true if the is code is 506.
     */
    public ResultMatcher isVariantAlsoNegotiates(){
        return is(HttpStatus.VARIANT_ALSO_NEGOTIATES);
    }

    /**
     * Convenience Method for {@link HttpStatus.INSUFFICIENT_STORAGE}
     * Check if the http is code is 507 or not.
     * @return true if the is code is 507.
     */
    public ResultMatcher isInsufficientStorage(){
        return is(HttpStatus.INSUFFICIENT_STORAGE);
    }

    /**
     * Convenience Method for {@link HttpStatus.LOOP_DETECTED}
     * Check if the http is code is 508 or not.
     * @return true if the is code is 508.
     */
    public ResultMatcher isLoopDetected(){
        return is(HttpStatus.LOOP_DETECTED);
    }

    /**
     * Convenience Method for {@link HttpStatus.NOT_EXTENDED}
     * Check if the http is code is 509 or not.
     * @return true if the is code is 509.
     */
    public ResultMatcher isNotExtended(){
        return is(HttpStatus.NOT_EXTENDED);
    }

}
