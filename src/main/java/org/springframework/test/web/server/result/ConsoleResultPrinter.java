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

import java.io.UnsupportedEncodingException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultPrinter;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A simple {@link ResultPrinter} that prints to {@code System.out}.
 * 
 * @author Rossen Stoyanchev
 */
public class ConsoleResultPrinter implements ResultPrinter {

	private static final int LABEL_WIDTH = 20;

	public ConsoleResultPrinter() {
	}

	public void print(MockHttpServletRequest request, 
					  MockHttpServletResponse response, 
					  Object handler,
					  HandlerInterceptor[] interceptors, 
					  ModelAndView mav, 
					  Exception exception) {
		
		System.out.println("-----------------------------------------");
		
		printRequest(request);
		printHandler(handler);
		printResolvedException(exception);
		printModelAndView(mav);
		printResponse(response);

		System.out.println();
	}

	protected void printRequest(MockHttpServletRequest request) {
		printHeading("HttpServletRequest");
		printValue("HTTP Method", request.getMethod());
		printValue("Request URI", request.getRequestURI());
		printValue("Params", ServletRequestMatchers.getParameterMap(request));
		printValue("Headers", ServletRequestMatchers.getHeaderValueMap(request));
	}

	private void printHeading(String text) {
		System.out.println();
		System.out.println(formatLabel(text, LABEL_WIDTH).append(":"));
	}

	protected void printValue(String label, Object value) {
		System.out.println(formatLabel(label, LABEL_WIDTH).append(" = ").append(value).toString());
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
	protected void printResponse(MockHttpServletResponse response) {
		printHeading("HttpServletResponse");
		printValue("status", response.getStatus());
		printValue("error message", response.getErrorMessage());
		printValue("headers", ServletResponseMatchers.getHeaderValueMap(response));
		printValue("content type", response.getContentType());
		printValue("body", getBody(response));
		printValue("forwarded URL", response.getForwardedUrl());
		printValue("redirected URL", response.getRedirectedUrl());
		printValue("included URLs", response.getIncludedUrls());
		printValue("cookies", ServletResponseMatchers.getCookieValueMap(response));
	}

	private String getBody(MockHttpServletResponse response) {
		try {
			String content = response.getContentAsString();
			if (StringUtils.hasLength(content) && (content.length() > 50)) {
				content = content.substring(0, 50) + " <trunkated> (50 of " + " " + content.length() + " chars)";
			}
			return content;
			
		} catch (UnsupportedEncodingException e) {
			return "Failed to get the response content: " + e.toString();
		}
	}

}
