/*
 * Copyright 2011-2012 the original author or authors.
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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * A sub-class of {@code DispatcherServlet} that saves the results of processing
 * a request in an {@link MvcResult}. The {@code MvcResult} instance is stored
 * the request attribute {@link TestDispatcherServlet#MVC_RESULT_ATTRIBUTE}.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 */
@SuppressWarnings("serial")
public final class TestDispatcherServlet extends DispatcherServlet {

	/**
	 * Create a new TestDispatcherServlet with the given {@code WebApplicationContext}.
	 */
	public TestDispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
	}

	protected DefaultMvcResult getMvcResult(ServletRequest request) {
		return (DefaultMvcResult) request.getAttribute(MockMvc.MVC_RESULT_ATTRIBUTE);
	}

	@Override
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		HandlerExecutionChain chain = super.getHandler(request);
		if (chain != null) {
			DefaultMvcResult mvcResult = getMvcResult(request);
			mvcResult.setHandler(chain.getHandler());
			mvcResult.setInterceptors(chain.getInterceptors());
		}
		return chain;
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DefaultMvcResult mvcResult = getMvcResult(request);
		mvcResult.setModelAndView(mv);
		super.render(mv, request, response);
	}

	@Override
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {

		ModelAndView mav = super.processHandlerException(request, response, handler, ex);

		// We got this far, exception was processed..
		DefaultMvcResult mvcResult = getMvcResult(request);
		mvcResult.setResolvedException(ex);
		mvcResult.setModelAndView(mav);

		return mav;
	}

}
