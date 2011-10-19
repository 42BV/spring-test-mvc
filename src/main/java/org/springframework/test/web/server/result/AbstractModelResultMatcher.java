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

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base class for Matchers that assert model attributes.
 */
public abstract class AbstractModelResultMatcher implements ResultMatcher {

	public final void match(MockHttpServletRequest request, 
							MockHttpServletResponse response, 
							Object handler,	
							HandlerInterceptor[] interceptors, 
							ModelAndView mav, 
							Exception resolvedException) throws Exception {
		
		AssertionErrors.assertTrue("No ModelAndView", mav != null);
		matchModel(mav.getModel());
	}

	protected abstract void matchModel(Map<String, Object> model) throws Exception;

}
