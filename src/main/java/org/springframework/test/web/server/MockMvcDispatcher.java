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

package org.springframework.test.web.server;

import static org.springframework.test.web.AssertionErrors.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * A "lightweight" alternative to the {@link DispatcherServlet} that executes requests without a Servlet container.
 * 
 * @author Rossen Stoyanchev
 */
class MockMvcDispatcher {
	
	private static Log logger = LogFactory.getLog(MockMvcDispatcher.class);
	
	private final MockMvcSetup mvcSetup;

	private Object handler;
	
	private HandlerInterceptor[] interceptors;
	
	private ModelAndView mav;
	
	private Exception resolvedException;
	
	/**
	 * TODO
	 * 
	 */
	private MockMvcDispatcher(MockMvcSetup setup) {
		this.mvcSetup = setup;
	}
	
	/**
	 * TODO
	 * 
	 */
	static MockMvcResult dispatch(final MockHttpServletRequest request, 
								  final MockHttpServletResponse response,
								  final MockMvcSetup setup, 
								  final boolean mapOnly) {
		
		final MockMvcDispatcher dispatcher = new MockMvcDispatcher(setup);
		dispatcher.execute(request, response, mapOnly);
		
		return new MockMvcResult() {
			
			public MockHttpServletRequest getRequest() {
				return request;
			}

			public MockHttpServletResponse getResponse() {
				return response;
			}

			public Object getController() {
				return dispatcher.handler;
			}

			public HandlerInterceptor[] getInterceptors() {
				return dispatcher.interceptors;
			}

			public ModelAndView getModelAndView() {
				return dispatcher.mav;
			}

			public Exception getResolvedException() {
				return dispatcher.resolvedException;
			}

			public boolean mapOnly() {
				return mapOnly;
			}
		};
	}

	/**
	 * Execute the request invoking the same Spring MVC components the {@link DispatcherServlet} does.
	 * 
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response, boolean mapOnly) {
		try {
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
			doExecute(request, response, mapOnly);
		}
		catch (Exception exception) {
			logger.error("Unhandled exception", exception);
			fail("Failed to dispatch Mock MVC request (check logs for stacktrace): " + exception);
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}
	}

	private void doExecute(HttpServletRequest request, HttpServletResponse response, boolean mapOnly) throws Exception {
		try {
			initHandlerExecutionChain(request);

			if (handler == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			if (mapOnly) {
				return;
			}
			
			List<HandlerInterceptor> interceptorList = (interceptors != null) ? 
					Arrays.asList(interceptors) : new ArrayList<HandlerInterceptor>();

			for (HandlerInterceptor interceptor : interceptorList) {
				if (!interceptor.preHandle(request, response, handler)) {
					return;
				}
			}

			HandlerAdapter adapter = getHandlerAdapter();
			mav = adapter.handle(request, response, handler);
			updateDefaultViewName(request);

			Collections.reverse(interceptorList);
			for (HandlerInterceptor interceptor : interceptorList) {
				interceptor.postHandle(request, response, handler, mav);
			}
		}
		catch (Exception exception) {
			processHandlerException(request, response, exception);
			updateDefaultViewName(request);
		}

		if (mav == null) {
			return;
		}

		Locale locale = mvcSetup.getLocaleResolver().resolveLocale(request);
		response.setLocale(locale);

		View view = resolveView(locale);
		view.render(mav.getModel(), request, response);
	}

	private void initHandlerExecutionChain(HttpServletRequest request) throws Exception {
		for (HandlerMapping mapping : mvcSetup.getHandlerMappings()) {
			HandlerExecutionChain chain = mapping.getHandler(request);
			if (chain != null) {
				handler = chain.getHandler();
				interceptors = chain.getInterceptors();
				return;
			}
		}
	}

	private HandlerAdapter getHandlerAdapter() {
		for (HandlerAdapter adapter : mvcSetup.getHandlerAdapters()) {
			if (adapter.supports(handler)) {
				return adapter;
			}
		}
		throw new IllegalStateException("No adapter for handler [" + handler
				+ "]. Available adapters: [" + mvcSetup.getHandlerAdapters() + "]");
	}

	private void updateDefaultViewName(HttpServletRequest request) throws Exception {
		if (mav != null && !mav.hasView()) {
			String viewName = mvcSetup.getViewNameTranslator().getViewName(request);
			mav.setViewName(viewName);
		}
	}

	private void processHandlerException(HttpServletRequest request, HttpServletResponse response, Exception exception) throws Exception {
		for (HandlerExceptionResolver resolver : mvcSetup.getExceptionResolvers()) {
			mav = resolver.resolveException(request, response, handler, exception);
			if (mav != null) {
				resolvedException = exception;
				mav = mav.isEmpty() ? null : mav;
				return;
			}
		}
		throw exception;
	}
	
	private View resolveView(Locale locale) throws Exception {
		if (mav.isReference()) {
			for (ViewResolver viewResolver : mvcSetup.getViewResolvers()) {
				View view = viewResolver.resolveViewName(mav.getViewName(), locale);
				if (view != null) {
					return view;
				}
			}
		}
		View view = mav.getView();
		Assert.isTrue(view != null, "Could not resolve view from ModelAndView: <" + mav + ">");
		return view;
	}

}
