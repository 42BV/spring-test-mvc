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

/**
 * A sub-class of DispatcherServlet that creates an {@link MvcResult} instance
 * at the start of a request, stores it in a request attribute, and populates
 * it as the request gets executed.
 *
 * <p>Use {@link #getMvcResult(HttpServletRequest)} to obtain the MvcResult for
 * an executed request.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings("serial")
public class TestDispatcherServlet extends DispatcherServlet {

	public static final String MVC_RESULT_ATTRIBUTE = TestDispatcherServlet.class.getName() + ".MVC_RESULT";

	/**
	 * Class constructor.
	 */
	public TestDispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
	}

	/**
	 * Return the MvcResult stored in the given request.
	 */
	public MvcResult getMvcResult(HttpServletRequest request) {
		return (MvcResult) request.getAttribute(MVC_RESULT_ATTRIBUTE);
	}

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Assert.isInstanceOf(MockHttpServletRequest.class, request,
				"Request should be MockHttpServletRequest: " + request.getClass().getName());
		Assert.isInstanceOf(MockHttpServletResponse.class, response,
				"Response should be MockHttpServletResponse" + response.getClass().getName());

		request.setAttribute(MVC_RESULT_ATTRIBUTE,
				new DefaultMvcResult((MockHttpServletRequest) request, (MockHttpServletResponse) response));

		super.doService(request, response);
	}

	@Override
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		HandlerExecutionChain chain = super.getHandler(request);
		if (chain != null) {
			DefaultMvcResult mvcResult = (DefaultMvcResult) getMvcResult(request);
			mvcResult.setHandler(chain.getHandler());
			mvcResult.setInterceptors(chain.getInterceptors());
		}
		return chain;
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DefaultMvcResult mvcResult = (DefaultMvcResult) getMvcResult(request);
		mvcResult.setModelAndView(mv);
		super.render(mv, request, response);
	}

	@Override
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {

		ModelAndView mav = super.processHandlerException(request, response, handler, ex);

		// We got this far, exception was processed..
		DefaultMvcResult mvcResult = (DefaultMvcResult) getMvcResult(request);
		mvcResult.setResolvedException(ex);
		mvcResult.setModelAndView(mav);

		return mav;
	}

	/**
	 * A simple implementation of MvcResult with getters and setters.
	 */
	private static class DefaultMvcResult implements MvcResult {

		private final MockHttpServletRequest request;

		private final MockHttpServletResponse response;

		private Object handler;

		private HandlerInterceptor[] interceptors;

		private ModelAndView mav;

		private Exception resolvedException;

		public DefaultMvcResult(MockHttpServletRequest request, MockHttpServletResponse response) {
			this.request = request;
			this.response = response;
		}

		public MockHttpServletRequest getRequest() {
			return this.request;
		}

		public MockHttpServletResponse getResponse() {
			return this.response;
		}

		public Object getHandler() {
			return this.handler;
		}

		public void setHandler(Object handler) {
			this.handler = handler;
		}

		public HandlerInterceptor[] getInterceptors() {
			return this.interceptors;
		}

		public void setInterceptors(HandlerInterceptor[] interceptors) {
			this.interceptors = interceptors;
		}

		public Exception getResolvedException() {
			return this.resolvedException;
		}

		public void setResolvedException(Exception resolvedException) {
			this.resolvedException = resolvedException;
		}

		public ModelAndView getModelAndView() {
			return this.mav;
		}

		public void setModelAndView(ModelAndView mav) {
			this.mav = mav;
		}

		public FlashMap getFlashMap() {
			return RequestContextUtils.getOutputFlashMap(request);
		}
	}
}
