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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

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
 * Executes requests by driving Spring MVC infrastructure components, much like the
 * DispatcherServlet does but outside a ServletContainer. 
 * 
 * <p>After the request is executed exposes information about the mapped handler, 
 * the resulting model and view, resolved exceptions, etc.
 * 
 * @author Rossen Stoyanchev
 */
class MockDispatcher {

	private final MvcSetup mvcSetup;

	private Object handler;
	
	private HandlerInterceptor[] interceptors;
	
	private ModelAndView mav;
	
	private Exception resolvedException;
	
	/**
	 * Package-private constructor used by {@link MockMvc}.
	 */
	MockDispatcher(MvcSetup setup) {
		this.mvcSetup = setup;
	}
	
	public Object getHandler() {
		return this.handler;
	}

	public HandlerInterceptor[] getInterceptors() {
		return this.interceptors;
	}

	public ModelAndView getMav() {
		return this.mav;
	}

	public Exception getResolvedException() {
		return this.resolvedException;
	}

	/**
	 * Execute the request invoking the same Spring MVC components the {@link DispatcherServlet} does.
	 * 
	 * @throws Exception if an exception occurs not handled by a HandlerExceptionResolver.
	 */
	public void execute(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
		try {
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
			this.mvcSetup.getFlashMapManager().requestStarted(request);
			doExecute(request, response);
		}
		finally {
			this.mvcSetup.getFlashMapManager().requestCompleted(request);
			RequestContextHolder.resetRequestAttributes();
		}
	}

	private void doExecute(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
		try {
			initHandlerExecutionChain(request);

			if (this.handler == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			List<HandlerInterceptor> interceptorList = (this.interceptors != null) ? 
					Arrays.asList(this.interceptors) : new ArrayList<HandlerInterceptor>();

			for (HandlerInterceptor interceptor : interceptorList) {
				if (!interceptor.preHandle(request, response, this.handler)) {
					return;
				}
			}

			HandlerAdapter adapter = getHandlerAdapter();
			this.mav = adapter.handle(request, response, this.handler);
			updateDefaultViewName(request);

			Collections.reverse(interceptorList);
			for (HandlerInterceptor interceptor : interceptorList) {
				interceptor.postHandle(request, response, this.handler, this.mav);
			}
		}
		catch (Exception exception) {
			processHandlerException(request, response, exception);
			updateDefaultViewName(request);
		}

		if (this.mav == null) {
			return;
		}

		Locale locale = this.mvcSetup.getLocaleResolver().resolveLocale(request);
		response.setLocale(locale);

		View view = resolveView(locale);
		view.render(this.mav.getModel(), request, response);
	}

	private void initHandlerExecutionChain(MockHttpServletRequest request) throws Exception {
		for (HandlerMapping mapping : this.mvcSetup.getHandlerMappings()) {
			HandlerExecutionChain chain = mapping.getHandler(request);
			if (chain != null) {
				this.handler = chain.getHandler();
				this.interceptors = chain.getInterceptors();
				return;
			}
		}
	}

	private HandlerAdapter getHandlerAdapter() {
		for (HandlerAdapter adapter : this.mvcSetup.getHandlerAdapters()) {
			if (adapter.supports(this.handler)) {
				return adapter;
			}
		}
		throw new IllegalStateException("No adapter for handler [" + handler
				+ "]. Available adapters: [" + mvcSetup.getHandlerAdapters() + "]");
	}

	private void updateDefaultViewName(MockHttpServletRequest request) throws Exception {
		if (this.mav != null && !this.mav.hasView()) {
			String viewName = this.mvcSetup.getViewNameTranslator().getViewName(request);
			this.mav.setViewName(viewName);
		}
	}

	private void processHandlerException(MockHttpServletRequest request, 
										 MockHttpServletResponse response, 
										 Exception exception) throws Exception {
		for (HandlerExceptionResolver resolver : this.mvcSetup.getExceptionResolvers()) {
			this.mav = resolver.resolveException(request, response, this.handler, exception);
			if (this.mav != null) {
				this.resolvedException = exception;
				this.mav = this.mav.isEmpty() ? null : this.mav;
				return;
			}
		}
		throw exception;
	}
	
	private View resolveView(Locale locale) throws Exception {
		if (this.mav.isReference()) {
			for (ViewResolver viewResolver : this.mvcSetup.getViewResolvers()) {
				View view = viewResolver.resolveViewName(this.mav.getViewName(), locale);
				if (view != null) {
					return view;
				}
			}
		}
		View view = this.mav.getView();
		Assert.isTrue(view != null, "Could not resolve view from ModelAndView: <" + this.mav + ">");
		return view;
	}

}
