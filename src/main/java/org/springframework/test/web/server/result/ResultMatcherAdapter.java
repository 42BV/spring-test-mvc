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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * A convenient base class for ResultMatcher implementations that allows sub-classes
 * to match one thing at a time -- the request, the response, etc.
 *
 * @author Rossen Stoyanchev
 */
public class ResultMatcherAdapter implements ResultMatcher {

	public final void match(MockHttpServletRequest request, 
							MockHttpServletResponse response, 
							Object handler,
							HandlerInterceptor[] interceptors, 
							ModelAndView mav, 
							Exception resolvedException) throws Exception {
		
		matchRequest(request);
		matchResponse(response);
		matchHandler(handler);
		matchHandlerInterceptors(interceptors);
		matchModelAndView(mav);
		matchFlashMap(RequestContextUtils.getOutputFlashMap(request));
		matchResolvedException(resolvedException);
	}

	/**
	 * Override to match the request. The default implementation is empty.
	 */
	protected void matchRequest(MockHttpServletRequest request) throws Exception {
		// Do nothing
	}

	/**
	 * Override to match the response. The default implementation is empty.
	 */
	protected void matchResponse(MockHttpServletResponse response) throws Exception {
		// Do nothing
	}

	/**
	 * Override to match the handler. The default implementation is empty.
	 */
	protected void matchHandler(Object handler) throws Exception {
		// Do nothing
	}

	/**
	 * Override to match the interceptors. The default implementation is empty.
	 */
	protected void matchHandlerInterceptors(HandlerInterceptor[] interceptors) throws Exception {
		// Do nothing
	}

	/**
	 * Override to match the model and the view. The default implementation is empty.
	 */
	protected void matchModelAndView(ModelAndView mav) throws Exception {
		// Do nothing
	}

	/**
	 * Override to match output flash attributes. The default implementation is empty.
	 */
	protected void matchFlashMap(FlashMap flashMap) throws Exception {
		// Do nothing
	}
	
	/**
	 * Override to match an exception resolved through a HandlerExceptionResolver. 
	 * The default implementation is empty.
	 */
	protected void matchResolvedException(Exception resolvedException) throws Exception {
		// Do nothing
	}
	
}
