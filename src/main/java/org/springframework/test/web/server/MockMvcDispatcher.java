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
 * A more "lightweight" alternative to the {@link DispatcherServlet} re-purposed for testing Spring MVC applications 
 * outside of a Servlet container environment in mind. Mimics the essential functionality of the DispatcherServlet 
 * but does not always behave in identical ways. For example invoking afterCompletion() on a HandlerInterceptor is 
 * not essential for integration testing since the same method can be unit tested.     
 * 
 * <p>Unlike the DispatcherServlet, the {@link MockMvcDispatcher} is stateful. It records contextual information during 
 * each invocation such as the request and the response, the mapped handler and handler interceptors, and the resulting 
 * ModelAndView. The recorded information may then be matched against application-specific expectations as defined by
 * {@link MvcResultActions}. Previously recorded context is cleared at the start of every dispatch invocation.
 * 
 * @NotThreadSafe
 */
public class MockMvcDispatcher {
	
	private Log logger = LogFactory.getLog(getClass());
	
	private final MvcSetup mvcSetup;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;
	
	private Object handler;
	
	private HandlerInterceptor[] interceptors;
	
	private ModelAndView mav;
	
	/**
	 * Create a {@link MockMvcDispatcher} with the provided {@link MvcSetup}.
	 */
	MockMvcDispatcher(MvcSetup setup) {
		this.mvcSetup = setup;
	}

	/**
	 * Process the request by invoking Spring MVC components in the {@link MvcSetup} provided to the constructor.
	 * The request may be partially processed if mapOnly is {@code true}.
	 * 
	 */
	public MvcResultActions dispatch(MockHttpServletRequest request, MockHttpServletResponse response, boolean mapOnly) {
		clear();
		this.request = request;
		this.response = response;
		
		try {
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
			doDispatch(mapOnly);
		}
		catch (Exception exception) {
			logger.error("Unhandled exception", exception);
			fail("Failed to dispatch request due to unhandled exception: " + exception);
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}

		return this.new ResultActionsAdapter();
	}

	private void clear() {
		request = null;
		response = null;
		handler = null;
		interceptors = null;
		mav = null;
	}

	private void doDispatch(boolean mapOnly) throws Exception {
		
		try {
			initHandlerExecutionChain();

			if (handler == null || mapOnly) {
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
			updateDefaultViewName();

			Collections.reverse(interceptorList);
			for (HandlerInterceptor interceptor : interceptorList) {
				interceptor.postHandle(request, response, handler, mav);
			}
		}
		catch (Exception exception) {
			processHandlerException(exception);
			updateDefaultViewName();
		}

		if (mav == null) {
			return;
		}

		Locale locale = mvcSetup.getLocaleResolver().resolveLocale(request);
		response.setLocale(locale);

		View view = resolveView(locale);
		view.render(mav.getModel(), request, response);
	}

	private void initHandlerExecutionChain() throws Exception {
		for (HandlerMapping mapping : mvcSetup.getHandlerMappings()) {
			HandlerExecutionChain chain = mapping.getHandler(request);
			if (chain != null) {
				handler = chain.getHandler();
				interceptors = chain.getInterceptors();
				return;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

	private void updateDefaultViewName() throws Exception {
		if (mav != null && !mav.hasView()) {
			String viewName = mvcSetup.getViewNameTranslator().getViewName(request);
			mav.setViewName(viewName);
		}
	}

	private void processHandlerException(Exception exception) throws Exception {
		for (HandlerExceptionResolver resolver : mvcSetup.getExceptionResolvers()) {
			mav = resolver.resolveException(request, response, handler, exception);
			if (mav != null) {
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

	private class ResultActionsAdapter implements MvcResultActions {

		public MvcResultActions andExpect(MvcResultMatcher matcher) {
			matcher.match(request, response, handler, mav);
			return this;
		}
		
	}

}
