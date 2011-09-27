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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultPrinter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Prints the results of an executed Spring MVC request to an {@link OutputStream}.
 * 
 * @author Rossen Stoyanchev
 */
public class DefaultResultPrinter implements ResultPrinter {

	private static final int LABEL_WIDTH = 20;
	
	private final PrintWriter writer;

	/**
	 * Protected constructor.
	 * @see MockMvcResultActions
	 */
	protected DefaultResultPrinter(OutputStream outputStream) {
		this.writer = new PrintWriter(outputStream);
	}

	public void print(MockHttpServletRequest request, 
					  MockHttpServletResponse response, 
					  Object handler,
					  HandlerInterceptor[] interceptors, 
					  ModelAndView mav, 
					  Exception exception) throws Exception {
		
		this.writer.println("-----------------------------------------");
		
		printRequest(request);
		printHandler(handler);
		printResolvedException(exception);
		printModelAndView(mav);
		printResponse(response);

		this.writer.println();
		this.writer.flush();
	}

	protected void printRequest(MockHttpServletRequest request) {
		printHeading("HttpServletRequest");
		printValue("HTTP Method", request.getMethod());
		printValue("Request URI", request.getRequestURI());
		printValue("Params", ResultMatcherUtils.requestParamsAsMap(request));
		printValue("Headers", ResultMatcherUtils.requestHeadersAsMap(request));
	}

	protected void printHeading(String text) {
		this.writer.println();
		this.writer.println(formatLabel(text, LABEL_WIDTH).append(":"));
	}

	protected void printValue(String label, Object value) {
		this.writer.println(formatLabel(label, LABEL_WIDTH).append(" = ").append(value).toString());
	}
	
	private StringBuilder formatLabel(String label, int width) {
		StringBuilder sb = new StringBuilder(label);
		while (sb.length() < width) {
			sb.insert(0, " ");
		}
		return sb;
	}

	/**
	 * Print the selected handler (likely an annotated controller method).
	 */
	protected void printHandler(Object handler) {
		printHeading("Handler");
		if (handler == null) {
			printValue("Type", "null (no matching handler found)");
			printValue("Method", null);
		}
		else {
			if (handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				printValue("Type", handlerMethod.getBeanType().getName());
				printValue("Method", handlerMethod);
			}
			else {
				printValue("Type", handler.getClass().getName());
				printValue("Method", "Unknown");
			}
		}
	}
	
	/**
	 * Print an exception raised in a controller and handled with a HandlerExceptionResolver, if any.
	 */
	protected void printResolvedException(Exception resolvedException) {
		printHeading("Resolved Exception");
		if (resolvedException == null) {
			printValue("Type", "null (not raised)");
		}
		else {
			printValue("Type", resolvedException.getClass().getName());
		}
	}

	/**
	 * Print the Model and view selection, or a brief message if view resolution was not required.
	 */
	protected void printModelAndView(ModelAndView mav) {
		printHeading("ModelAndView");
		if (mav == null) {
			printValue("View", "null (view resolution was not required)");
			printValue("Attributes", "null (view resolution was not required)");
		}
		else {
			printValue("View", mav.isReference() ? mav.getViewName() : mav.getView());
			if (mav.getModel().size() == 0) {
				printValue("Attributes", null);
			}
			for (String name : mav.getModel().keySet()) {
				if (name.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
					continue;
				}
				Object value = mav.getModel().get(name);
				Errors errors = (Errors) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
				printValue("Attribute", name);
				printValue("value", value);
				if (errors != null) {
					printValue("errors", errors.getAllErrors());
				}
			}
		}
	}

	/**
	 * Print the HttpServletResponse.
	 */
	protected void printResponse(MockHttpServletResponse response) throws UnsupportedEncodingException {
		printHeading("HttpServletResponse");
		printValue("status", response.getStatus());
		printValue("error message", response.getErrorMessage());
		printValue("headers", ResultMatcherUtils.headersAsMap(response));
		printValue("content type", response.getContentType());
		printValue("body", response.getContentAsString());
		printValue("forwarded URL", response.getForwardedUrl());
		printValue("redirected URL", response.getRedirectedUrl());
		printValue("included URLs", response.getIncludedUrls());
		printValue("cookies", ResultMatcherUtils.cookiesAsMap(response));
	}

}
