package org.springframework.test.web.server.result;

import static org.springframework.test.web.AssertionErrors.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;

/**
 * Provides methods to define expectations on the HttpStatus
 * 
 * @author Keesun Baik
 */
public class StatusResultMatchers {

    /**
	 * Match the expected response status to that of the HttpServletResponse
	 */
    public ResultMatcher is(final HttpStatus status) {
		return new AbstractServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", status, HttpStatus.valueOf(response.getStatus()));
			}
		};
	}

	/**
	 * Match the expected response status and reason to those set in the HttpServletResponse
	 * via {@link HttpServletResponse#sendError(int, String)}. 
	 */
	public ResultMatcher is(final HttpStatus expectedStatus, final String expectedReason) {
		return new AbstractServletResponseResultMatcher() {
			protected void matchResponse(MockHttpServletResponse response) {
				assertEquals("Status", expectedStatus, HttpStatus.valueOf(response.getStatus()));
				assertEquals("Reason", expectedReason, response.getErrorMessage());
			}
		};
	}

    /**
     * Convenience Method for {@code HttpStatus.OK}
     * Check if the HTTP is code is 200 or not.
     * @return true if the is code is 200.
     */
    public ResultMatcher isOk(){
        return is(HttpStatus.OK);
    }

    /**
     * Convenience Method for {@code HttpStatus.OK}
     * Check if the HTTP is code is 200 and the message.
     * @param reason expected message.
     * @return true if the is code is 200 and the message is equal.
     */
    public ResultMatcher isOk(String reason){
        return is(HttpStatus.NOT_FOUND, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_FOUND}
     * Check if the HTTP is code is 404 or not.
     * @return true if the is code is 404.
     */
    public ResultMatcher isNotFound(){
        return is(HttpStatus.NOT_FOUND);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_FOUND}
     * Check if the HTTP is code is 404 and the error message.
     * @param reason expected error message.
     * @return true if the is code is 404 and the error message is equal.
     */
    public ResultMatcher isNotFound(String reason){
        return is(HttpStatus.NOT_FOUND, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.CONTINUE}
     * Check if the HTTP is code is 100 or not.
     * @return true if the is code is 100.
     */
    public ResultMatcher isContinue(){
        return is(HttpStatus.CONTINUE);
    }

    /**
     * Convenience Method for {@code HttpStatus.CONTINUE}
     * Check if the HTTP is code is 100 and the message.
     * @param reason expected message.
     * @return true if the is code is 100 and the message is equal.
     */
    public ResultMatcher isContinue(String reason){
        return is(HttpStatus.CONTINUE);
    }

    /**
     * Convenience Method for {@code HttpStatus.SWITCHING_PROTOCOLS}
     * Check if the HTTP is code is 101 or not.
     * @return true if the is code is 101.
     */
    public ResultMatcher isSwitchingProtocols(){
        return is(HttpStatus.SWITCHING_PROTOCOLS);
    }

    /**
     * Convenience Method for {@code HttpStatus.SWITCHING_PROTOCOLS}
     * Check if the HTTP is code is 101 and the message.
     * @param reason expected message.
     * @return true if the is code is 101 and the message is equal.
     */
    public ResultMatcher isSwitchingProtocols(String reason){
        return is(HttpStatus.SWITCHING_PROTOCOLS, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.PROCESSING}
     * Check if the HTTP is code is 102 or not.
     * @return true if the is code is 102.
     */
    public ResultMatcher isProcessing(){
        return is(HttpStatus.PROCESSING);
    }

    /**
     * Convenience Method for {@code HttpStatus.PROCESSING}
     * Check if the HTTP is code is 102 and the message.
     * @param reason expected message.
     * @return true if the is code is 102 and the message is equal.
     */
    public ResultMatcher isProcessing(String reason){
        return is(HttpStatus.PROCESSING, reason);
    }


    /**
     * Convenience Method for {@code HttpStatus.CREATED}
     * Check if the HTTP is code is 201 or not.
     * @return true if the is code is 201.
     */
    public ResultMatcher isCreated(){
        return is(HttpStatus.CREATED);
    }

    /**
     * Convenience Method for {@code HttpStatus.CREATED}
     * Check if the HTTP is code is 201 and the message.
     * @param reason expected message.
     * @return true if the is code is 201 and the message is equal.
     */
    public ResultMatcher isCreated(String reason){
        return is(HttpStatus.CREATED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.ACCEPTED}
     * Check if the HTTP is code is 202 or not.
     * @return true if the is code is 202.
     */
    public ResultMatcher isAccepted(){
        return is(HttpStatus.ACCEPTED);
    }

    /**
     * Convenience Method for {@code HttpStatus.ACCEPTED}
     * Check if the HTTP is code is 202 and the message.
     * @param reason expected message.
     * @return true if the is code is 202 and the message is equal.
     */
    public ResultMatcher isAccepted(String reason){
        return is(HttpStatus.ACCEPTED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NON_AUTHORITATIVE_INFORMATION}
     * Check if the HTTP is code is 203 or not.
     * @return true if the is code is 203.
     */
    public ResultMatcher isNonAuthoritativeInformation(){
        return is(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    /**
     * Convenience Method for {@code HttpStatus.NON_AUTHORITATIVE_INFORMATION}
     * Check if the HTTP is code is 203 and the message.
     * @param reason expected message.
     * @return true if the is code is 203 and the message is equal.
     */
    public ResultMatcher isNonAuthoritativeInformation(String reason){
        return is(HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NO_CONTENT}
     * Check if the HTTP is code is 204 or not.
     * @return true if the is code is 204.
     */
    public ResultMatcher isNoContent(){
        return is(HttpStatus.NO_CONTENT);
    }

    /**
     * Convenience Method for {@code HttpStatus.NO_CONTENT}
     * Check if the HTTP is code is 204 and the message.
     * @param reason expected message.
     * @return true if the is code is 204 and the message is equal.
     */
    public ResultMatcher isNoContent(String reason){
        return is(HttpStatus.NO_CONTENT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.RESET_CONTENT}
     * Check if the HTTP is code is 205 or not.
     * @return true if the is code is 205.
     */
    public ResultMatcher isResetContent(){
        return is(HttpStatus.RESET_CONTENT);
    }

    /**
     * Convenience Method for {@code HttpStatus.RESET_CONTENT}
     * Check if the HTTP is code is 205 and the message.
     * @param reason expected message.
     * @return true if the is code is 205 and the message is equal.
     */
    public ResultMatcher isResetContent(String reason){
        return is(HttpStatus.RESET_CONTENT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.PARTIAL_CONTENT}
     * Check if the HTTP is code is 206 or not.
     * @return true if the is code is 206.
     */
    public ResultMatcher isPartialContent(){
        return is(HttpStatus.PARTIAL_CONTENT);
    }

    /**
     * Convenience Method for {@code HttpStatus.PARTIAL_CONTENT}
     * Check if the HTTP is code is 206 and the message.
     * @param reason expected message.
     * @return true if the is code is 206 and the message is equal.
     */
    public ResultMatcher isPartialContent(String reason){
        return is(HttpStatus.PARTIAL_CONTENT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.MULTI_STATUS}
     * Check if the HTTP is code is 207 or not.
     * @return true if the is code is 207.
     */
    public ResultMatcher isMultiStatus(){
        return is(HttpStatus.MULTI_STATUS);
    }

    /**
     * Convenience Method for {@code HttpStatus.MULTI_STATUS}
     * Check if the HTTP is code is 207 and the message.
     * @param reason expected message.
     * @return true if the is code is 207 and the message is equal.
     */
    public ResultMatcher isMultiStatus(String reason){
        return is(HttpStatus.MULTI_STATUS, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.ALREADY_REPORTED}
     * Check if the HTTP is code is 208 or not.
     * @return true if the is code is 208.
     */
    public ResultMatcher isAlreadyReported(){
        return is(HttpStatus.ALREADY_REPORTED);
    }

    /**
     * Convenience Method for {@code HttpStatus.ALREADY_REPORTED}
     * Check if the HTTP is code is 208 and the message.
     * @param reason expected message.
     * @return true if the is code is 208 and the message is equal.
     */
    public ResultMatcher isAlreadyReported(String reason){
        return is(HttpStatus.ALREADY_REPORTED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.IM_USED}
     * Check if the HTTP is code is 226 or not.
     * @return true if the is code is 226.
     */
    public ResultMatcher isImUsed(){
        return is(HttpStatus.IM_USED);
    }

    /**
     * Convenience Method for {@code HttpStatus.IM_USED}
     * Check if the HTTP is code is 226 and the message.
     * @param reason expected message.
     * @return true if the is code is 226 and the message is equal.
     */
    public ResultMatcher isImUsed(String reason){
        return is(HttpStatus.IM_USED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.MULTIPLE_CHOICES}
     * Check if the HTTP is code is 300 or not.
     * @return true if the is code is 300.
     */
    public ResultMatcher isMultipleChoices(){
        return is(HttpStatus.MULTIPLE_CHOICES);
    }

    /**
     * Convenience Method for {@code HttpStatus.MULTIPLE_CHOICES}
     * Check if the HTTP is code is 300 and the message.
     * @param reason expected message.
     * @return true if the is code is 300 and the message is equal.
     */
    public ResultMatcher isMultipleChoices(String reason){
        return is(HttpStatus.MULTIPLE_CHOICES, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.MOVED_PERMANENTLY}
     * Check if the HTTP is code is 301 or not.
     * @return true if the is code is 301.
     */
    public ResultMatcher isMovedPermanently(){
        return is(HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Convenience Method for {@code HttpStatus.MOVED_PERMANENTLY}
     * Check if the HTTP is code is 301 and the message.
     * @param reason expected message.
     * @return true if the is code is 301 and the message is equal.
     */
    public ResultMatcher isMovedPermanently(String reason){
        return is(HttpStatus.MOVED_PERMANENTLY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.FOUND}
     * Check if the HTTP is code is 302 or not.
     * @return true if the is code is 302.
     */
    public ResultMatcher isFound(){
        return is(HttpStatus.FOUND);
    }

    /**
     * Convenience Method for {@code HttpStatus.FOUND}
     * Check if the HTTP is code is 302 and the message.
     * @param reason expected message.
     * @return true if the is code is 302 and the message is equal.
     */
    public ResultMatcher isFound(String reason){
        return is(HttpStatus.FOUND, reason);
    }


    /**
     * Convenience Method for {@code HttpStatus.MOVED_TEMPORARILY}
     * Check if the HTTP is code is 302 or not.
     * @return true if the is code is 302.
     */
    public ResultMatcher isMovedTemporarily(){
        return is(HttpStatus.MOVED_TEMPORARILY);
    }

    /**
     * Convenience Method for {@code HttpStatus.MOVED_TEMPORARILY}
     * Check if the HTTP is code is 302 and the message.
     * @param reason expected message.
     * @return true if the is code is 302 and the message is equal.
     */
    public ResultMatcher isMovedTemporarily(String reason){
        return is(HttpStatus.MOVED_TEMPORARILY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.SEE_OTHER}
     * Check if the HTTP is code is 303 or not.
     * @return true if the is code is 303.
     */
    public ResultMatcher isSeeOther(){
        return is(HttpStatus.SEE_OTHER);
    }

    /**
     * Convenience Method for {@code HttpStatus.SEE_OTHER}
     * Check if the HTTP is code is 303 and the message.
     * @param reason expected message.
     * @return true if the is code is 303 and the message is equal.
     */
    public ResultMatcher isSeeOther(String reason){
        return is(HttpStatus.SEE_OTHER, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_MODIFIED}
     * Check if the HTTP is code is 304 or not.
     * @return true if the is code is 304.
     */
    public ResultMatcher isNotModified(){
        return is(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_MODIFIED}
     * Check if the HTTP is code is 304 and the message.
     * @param reason expected message.
     * @return true if the is code is 304 and the message is equal.
     */
    public ResultMatcher isNotModified(String reason){
        return is(HttpStatus.NOT_MODIFIED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.USE_PROXY}
     * Check if the HTTP is code is 305 or not.
     * @return true if the is code is 305.
     */
    public ResultMatcher isUseProxy(){
        return is(HttpStatus.USE_PROXY);
    }

    /**
     * Convenience Method for {@code HttpStatus.USE_PROXY}
     * Check if the HTTP is code is 305 and the message.
     * @param reason expected message.
     * @return true if the is code is 305 and the message is equal.
     */
    public ResultMatcher isUseProxy(String reason){
        return is(HttpStatus.USE_PROXY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.TEMPORARY_REDIRECT}
     * Check if the HTTP is code is 307 or not.
     * @return true if the is code is 307.
     */
    public ResultMatcher isTemporaryRedirect(){
        return is(HttpStatus.TEMPORARY_REDIRECT);
    }

    /**
     * Convenience Method for {@code HttpStatus.TEMPORARY_REDIRECT}
     * Check if the HTTP is code is 307 and the message.
     * @param reason expected message.
     * @return true if the is code is 307 and the message is equal.
     */
    public ResultMatcher isTemporaryRedirect(String reason){
        return is(HttpStatus.TEMPORARY_REDIRECT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.BAD_REQUEST}
     * Check if the HTTP is code is 400 or not.
     * @return true if the is code is 400.
     */
    public ResultMatcher isBadRequest(){
        return is(HttpStatus.BAD_REQUEST);
    }

    /**
     * Convenience Method for {@code HttpStatus.BAD_REQUEST}
     * Check if the HTTP is code is 400 and the message.
     * @param reason expected message.
     * @return true if the is code is 400 and the message is equal.
     */
    public ResultMatcher isBadRequest(String reason){
        return is(HttpStatus.BAD_REQUEST, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNAUTHORIZED}
     * Check if the HTTP is code is 401 or not.
     * @return true if the is code is 401.
     */
    public ResultMatcher isUnauthorized(){
        return is(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNAUTHORIZED}
     * Check if the HTTP is code is 401 and the message.
     * @param reason expected message.
     * @return true if the is code is 401 and the message is equal.
     */
    public ResultMatcher isUnauthorized(String reason){
        return is(HttpStatus.UNAUTHORIZED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.PAYMENT_REQUIRED}
     * Check if the HTTP is code is 402 or not.
     * @return true if the is code is 402.
     */
    public ResultMatcher isPaymentRequired(){
        return is(HttpStatus.PAYMENT_REQUIRED);
    }

    /**
     * Convenience Method for {@code HttpStatus.PAYMENT_REQUIRED}
     * Check if the HTTP is code is 402 and the message.
     * @param reason expected message.
     * @return true if the is code is 402 and the message is equal.
     */
    public ResultMatcher isPaymentRequired(String reason){
        return is(HttpStatus.PAYMENT_REQUIRED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.FORBIDDEN}
     * Check if the HTTP is code is 403 or not.
     * @return true if the is code is 403.
     */
    public ResultMatcher isForbidden(){
        return is(HttpStatus.FORBIDDEN);
    }

    /**
     * Convenience Method for {@code HttpStatus.FORBIDDEN}
     * Check if the HTTP is code is 403 and the message.
     * @param reason expected message.
     * @return true if the is code is 403 and the message is equal.
     */
    public ResultMatcher isForbidden(String reason){
        return is(HttpStatus.FORBIDDEN, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.METHOD_NOT_ALLOWED}
     * Check if the HTTP is code is 405 or not.
     * @return true if the is code is 405.
     */
    public ResultMatcher isMethodNotAllowed(){
        return is(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Convenience Method for {@code HttpStatus.METHOD_NOT_ALLOWED}
     * Check if the HTTP is code is 405 and the message.
     * @param reason expected message.
     * @return true if the is code is 405 and the message is equal.
     */
    public ResultMatcher isMethodNotAllowed(String reason){
        return is(HttpStatus.METHOD_NOT_ALLOWED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_ACCEPTABLE}
     * Check if the HTTP is code is 406 or not.
     * @return true if the is code is 406.
     */
    public ResultMatcher isNotAcceptable(){
        return is(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_ACCEPTABLE}
     * Check if the HTTP is code is 406 and the message.
     * @param reason expected message.
     * @return true if the is code is 406 and the message is equal.
     */
    public ResultMatcher isNotAcceptable(String reason){
        return is(HttpStatus.NOT_ACCEPTABLE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.PROXY_AUTHENTICATION_REQUIRED}
     * Check if the HTTP is code is 407 or not.
     * @return true if the is code is 407.
     */
    public ResultMatcher isProxyAuthenticationRequired(){
        return is(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    /**
     * Convenience Method for {@code HttpStatus.PROXY_AUTHENTICATION_REQUIRED}
     * Check if the HTTP is code is 407 and the message.
     * @param reason expected message.
     * @return true if the is code is 407 and the message is equal.
     */
    public ResultMatcher isProxyAuthenticationRequired(String reason){
        return is(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_TIMEOUT}
     * Check if the HTTP is code is 408 or not.
     * @return true if the is code is 408.
     */
    public ResultMatcher isRequestTimeout(){
        return is(HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_TIMEOUT}
     * Check if the HTTP is code is 408 and the message.
     * @param reason expected message.
     * @return true if the is code is 408 and the message is equal.
     */
    public ResultMatcher isRequestTimeout(String reason){
        return is(HttpStatus.REQUEST_TIMEOUT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.CONFLICT}
     * Check if the HTTP is code is 409 or not.
     * @return true if the is code is 409.
     */
    public ResultMatcher isConflict(){
        return is(HttpStatus.CONFLICT);
    }

    /**
     * Convenience Method for {@code HttpStatus.CONFLICT}
     * Check if the HTTP is code is 409 and the message.
     * @param reason expected message.
     * @return true if the is code is 409 and the message is equal.
     */
    public ResultMatcher isConflict(String reason){
        return is(HttpStatus.CONFLICT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.GONE}
     * Check if the HTTP is code is 410 or not.
     * @return true if the is code is 410.
     */
    public ResultMatcher isGone(){
        return is(HttpStatus.GONE);
    }

    /**
     * Convenience Method for {@code HttpStatus.GONE}
     * Check if the HTTP is code is 410 and the message.
     * @param reason expected message.
     * @return true if the is code is 410 and the message is equal.
     */
    public ResultMatcher isGone(String reason){
        return is(HttpStatus.GONE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.LENGTH_REQUIRED}
     * Check if the HTTP is code is 411 or not.
     * @return true if the is code is 411.
     */
    public ResultMatcher isLengthRequired(){
        return is(HttpStatus.LENGTH_REQUIRED);
    }

    /**
     * Convenience Method for {@code HttpStatus.LENGTH_REQUIRED}
     * Check if the HTTP is code is 411 and the message.
     * @param reason expected message.
     * @return true if the is code is 411 and the message is equal.
     */
    public ResultMatcher isLengthRequired(String reason){
        return is(HttpStatus.LENGTH_REQUIRED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.PRECONDITION_FAILED}
     * Check if the HTTP is code is 412 or not.
     * @return true if the is code is 412.
     */
    public ResultMatcher isPreconditionFailed(){
        return is(HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * Convenience Method for {@code HttpStatus.PRECONDITION_FAILED}
     * Check if the HTTP is code is 412 and the message.
     * @param reason expected message.
     * @return true if the is code is 412 and the message is equal.
     */
    public ResultMatcher isPreconditionFailed(String reason){
        return is(HttpStatus.PRECONDITION_FAILED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_ENTITY_TOO_LARGE}
     * Check if the HTTP is code is 413 or not.
     * @return true if the is code is 413.
     */
    public ResultMatcher isRequestEntityTooLarge(){
        return is(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_ENTITY_TOO_LARGE}
     * Check if the HTTP is code is 413 and the message.
     * @param reason expected message.
     * @return true if the is code is 413 and the message is equal.
     */
    public ResultMatcher isRequestEntityTooLarge(String reason){
        return is(HttpStatus.REQUEST_ENTITY_TOO_LARGE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_URI_TOO_LONG}
     * Check if the HTTP is code is 414 or not.
     * @return true if the is code is 414.
     */
    public ResultMatcher isRequestUriTooLong(){
        return is(HttpStatus.REQUEST_URI_TOO_LONG);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUEST_URI_TOO_LONG}
     * Check if the HTTP is code is 414 and the message.
     * @param reason expected message.
     * @return true if the is code is 414 and the message is equal.
     */
    public ResultMatcher isRequestUriTooLong(String reason){
        return is(HttpStatus.REQUEST_URI_TOO_LONG, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNSUPPORTED_MEDIA_TYPE}
     * Check if the HTTP is code is 415 or not.
     * @return true if the is code is 415.
     */
    public ResultMatcher isUnsupportedMediaType(){
        return is(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNSUPPORTED_MEDIA_TYPE}
     * Check if the HTTP is code is 415 and the message.
     * @param reason expected message.
     * @return true if the is code is 415 and the message is equal.
     */
    public ResultMatcher isUnsupportedMediaType(String reason){
        return is(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE}
     * Check if the HTTP is code is 416 or not.
     * @return true if the is code is 416.
     */
    public ResultMatcher isRequestedRangeNotSatisfiable(){
        return is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * Convenience Method for {@code HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE}
     * Check if the HTTP is code is 416 and the message.
     * @param reason expected message.
     * @return true if the is code is 416 and the message is equal.
     */
    public ResultMatcher isRequestedRangeNotSatisfiable(String reason){
        return is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.EXPECTATION_FAILED}
     * Check if the HTTP is code is 417 or not.
     * @return true if the is code is 417.
     */
    public ResultMatcher isExpectationFailed(){
        return is(HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * Convenience Method for {@code HttpStatus.EXPECTATION_FAILED}
     * Check if the HTTP is code is 417 and the message.
     * @param reason expected message.
     * @return true if the is code is 417 and the message is equal.
     */
    public ResultMatcher isExpectationFailed(String reason){
        return is(HttpStatus.EXPECTATION_FAILED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE}
     * Check if the HTTP is code is 419 or not.
     * @return true if the is code is 419.
     */
    public ResultMatcher isInsufficientSpaceOnResource(){
        return is(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
    }

    /**
     * Convenience Method for {@code HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE}
     * Check if the HTTP is code is 419 and the message.
     * @param reason expected message.
     * @return true if the is code is 419 and the message is equal.
     */
    public ResultMatcher isInsufficientSpaceOnResource(String reason){
        return is(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
    }

    /**
     * Convenience Method for {@code HttpStatus.METHOD_FAILURE}
     * Check if the HTTP is code is 420 or not.
     * @return true if the is code is 420.
     */
    public ResultMatcher isMethodFailure(){
        return is(HttpStatus.METHOD_FAILURE);
    }

    /**
     * Convenience Method for {@code HttpStatus.METHOD_FAILURE}
     * Check if the HTTP is code is 420 and the message.
     * @param reason expected message.
     * @return true if the is code is 420 and the message is equal.
     */
    public ResultMatcher isMethodFailure(String reason){
        return is(HttpStatus.METHOD_FAILURE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.DESTINATION_LOCKED}
     * Check if the HTTP is code is 421 or not.
     * @return true if the is code is 421.
     */
    public ResultMatcher isDestinationLocked(){
        return is(HttpStatus.DESTINATION_LOCKED);
    }

    /**
     * Convenience Method for {@code HttpStatus.DESTINATION_LOCKED}
     * Check if the HTTP is code is 421 and the message.
     * @param reason expected message.
     * @return true if the is code is 421 and the message is equal.
     */
    public ResultMatcher isDestinationLocked(String reason){
        return is(HttpStatus.DESTINATION_LOCKED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNPROCESSABLE_ENTITY}
     * Check if the HTTP is code is 422 or not.
     * @return true if the is code is 422.
     */
    public ResultMatcher isUnprocessableEntity(){
        return is(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Convenience Method for {@code HttpStatus.UNPROCESSABLE_ENTITY}
     * Check if the HTTP is code is 422 and the message.
     * @param reason expected message.
     * @return true if the is code is 422 and the message is equal.
     */
    public ResultMatcher isUnprocessableEntity(String reason){
        return is(HttpStatus.UNPROCESSABLE_ENTITY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.LOCKED}
     * Check if the HTTP is code is 423 or not.
     * @return true if the is code is 423.
     */
    public ResultMatcher isLocked(){
        return is(HttpStatus.LOCKED);
    }

    /**
     * Convenience Method for {@code HttpStatus.LOCKED}
     * Check if the HTTP is code is 423 and the message.
     * @param reason expected message.
     * @return true if the is code is 423 and the message is equal.
     */
    public ResultMatcher isLocked(String reason){
        return is(HttpStatus.LOCKED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.FAILED_DEPENDENCY}
     * Check if the HTTP is code is 424 or not.
     * @return true if the is code is 424.
     */
    public ResultMatcher isFailedDependency(){
        return is(HttpStatus.FAILED_DEPENDENCY);
    }

    /**
     * Convenience Method for {@code HttpStatus.FAILED_DEPENDENCY}
     * Check if the HTTP is code is 424 and the message.
     * @param reason expected message.
     * @return true if the is code is 424 and the message is equal.
     */
    public ResultMatcher isFailedDependency(String reason){
        return is(HttpStatus.FAILED_DEPENDENCY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.UPGRADE_REQUIRED}
     * Check if the HTTP is code is 426 or not.
     * @return true if the is code is 426.
     */
    public ResultMatcher isUpgradeRequired(){
        return is(HttpStatus.UPGRADE_REQUIRED);
    }

    /**
     * Convenience Method for {@code HttpStatus.UPGRADE_REQUIRED}
     * Check if the HTTP is code is 426 and the message.
     * @param reason expected message.
     * @return true if the is code is 426 and the message is equal.
     */
    public ResultMatcher isUpgradeRequired(String reason){
        return is(HttpStatus.UPGRADE_REQUIRED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.INTERNAL_SERVER_ERROR}
     * Check if the HTTP is code is 500 or not.
     * @return true if the is code is 500.
     */
    public ResultMatcher isInternalServerError(){
        return is(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Convenience Method for {@code HttpStatus.INTERNAL_SERVER_ERROR}
     * Check if the HTTP is code is 500 and the message.
     * @param reason expected message.
     * @return true if the is code is 500 and the message is equal.
     */
    public ResultMatcher isInternalServerError(String reason){
        return is(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_IMPLEMENTED}
     * Check if the HTTP is code is 501 or not.
     * @return true if the is code is 501.
     */
    public ResultMatcher isNotImplemented(){
        return is(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_IMPLEMENTED}
     * Check if the HTTP is code is 501 and the message.
     * @param reason expected message.
     * @return true if the is code is 501 and the message is equal.
     */
    public ResultMatcher isNotImplemented(String reason){
        return is(HttpStatus.NOT_IMPLEMENTED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.BAD_GATEWAY}
     * Check if the HTTP is code is 502 or not.
     * @return true if the is code is 502.
     */
    public ResultMatcher isBadGateway(){
        return is(HttpStatus.BAD_GATEWAY);
    }

    /**
     * Convenience Method for {@code HttpStatus.BAD_GATEWAY}
     * Check if the HTTP is code is 502 and the message.
     * @param reason expected message.
     * @return true if the is code is 502 and the message is equal.
     */
    public ResultMatcher isBadGateway(String reason){
        return is(HttpStatus.BAD_GATEWAY, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.SERVICE_UNAVAILABLE}
     * Check if the HTTP is code is 503 or not.
     * @return true if the is code is 503.
     */
    public ResultMatcher isServiceUnavailable(){
        return is(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Convenience Method for {@code HttpStatus.SERVICE_UNAVAILABLE}
     * Check if the HTTP is code is 503 and the message.
     * @param reason expected message.
     * @return true if the is code is 503 and the message is equal.
     */
    public ResultMatcher isServiceUnavailable(String reason){
        return is(HttpStatus.SERVICE_UNAVAILABLE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.GATEWAY_TIMEOUT}
     * Check if the HTTP is code is 504 or not.
     * @return true if the is code is 504.
     */
    public ResultMatcher isGatewayTimeout(){
        return is(HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Convenience Method for {@code HttpStatus.GATEWAY_TIMEOUT}
     * Check if the HTTP is code is 504 and the message.
     * @param reason expected message.
     * @return true if the is code is 504 and the message is equal.
     */
    public ResultMatcher isGatewayTimeout(String reason){
        return is(HttpStatus.GATEWAY_TIMEOUT, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.HTTP_VERSION_NOT_SUPPORTED}
     * Check if the HTTP is code is 505 or not.
     * @return true if the is code is 505.
     */
    public ResultMatcher isHttpVersionNotSupported(){
        return is(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }

    /**
     * Convenience Method for {@code HttpStatus.HTTP_VERSION_NOT_SUPPORTED}
     * Check if the HTTP is code is 505 and the message.
     * @param reason expected message.
     * @return true if the is code is 505 and the message is equal.
     */
    public ResultMatcher isHttpVersionNotSupported(String reason){
        return is(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.VARIANT_ALSO_NEGOTIATES}
     * Check if the HTTP is code is 506 or not.
     * @return true if the is code is 506.
     */
    public ResultMatcher isVariantAlsoNegotiates(){
        return is(HttpStatus.VARIANT_ALSO_NEGOTIATES);
    }

    /**
     * Convenience Method for {@code HttpStatus.VARIANT_ALSO_NEGOTIATES}
     * Check if the HTTP is code is 506 and the message.
     * @param reason expected message.
     * @return true if the is code is 506 and the message is equal.
     */
    public ResultMatcher isVariantAlsoNegotiates(String reason){
        return is(HttpStatus.VARIANT_ALSO_NEGOTIATES, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.INSUFFICIENT_STORAGE}
     * Check if the HTTP is code is 507 or not.
     * @return true if the is code is 507.
     */
    public ResultMatcher isInsufficientStorage(){
        return is(HttpStatus.INSUFFICIENT_STORAGE);
    }

    /**
     * Convenience Method for {@code HttpStatus.INSUFFICIENT_STORAGE}
     * Check if the HTTP is code is 507 and the message.
     * @param reason expected message.
     * @return true if the is code is 507 and the message is equal.
     */
    public ResultMatcher isInsufficientStorage(String reason){
        return is(HttpStatus.INSUFFICIENT_STORAGE, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.LOOP_DETECTED}
     * Check if the HTTP is code is 508 or not.
     * @return true if the is code is 508.
     */
    public ResultMatcher isLoopDetected(){
        return is(HttpStatus.LOOP_DETECTED);
    }

    /**
     * Convenience Method for {@code HttpStatus.LOOP_DETECTED}
     * Check if the HTTP is code is 508 and the message.
     * @param reason expected message.
     * @return true if the is code is 508 and the message is equal.
     */
    public ResultMatcher isLoopDetected(String reason){
        return is(HttpStatus.LOOP_DETECTED, reason);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_EXTENDED}
     * Check if the HTTP is code is 509 or not.
     * @return true if the is code is 509.
     */
    public ResultMatcher isNotExtended(){
        return is(HttpStatus.NOT_EXTENDED);
    }

    /**
     * Convenience Method for {@code HttpStatus.NOT_EXTENDED}
     * Check if the HTTP is code is 509 and the message.
     * @param reason expected message.
     * @return true if the is code is 509 and the message is equal.
     */
    public ResultMatcher isNotExtended(String reason){
        return is(HttpStatus.NOT_EXTENDED, reason);
    }

}
