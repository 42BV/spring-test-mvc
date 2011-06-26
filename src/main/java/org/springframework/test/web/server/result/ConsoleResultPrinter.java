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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MockMvcResult;
import org.springframework.test.web.server.MockMvcResultPrinter;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO
 * 
 * @author Rossen Stoyanchev
 */
public class ConsoleResultPrinter implements MockMvcResultPrinter {

	private static final int LABEL_WIDTH = 17;

	ConsoleResultPrinter() {
	}

	public void print(MockMvcResult result) {
		
		System.out.println("-----------------------------------------");
		
		printRequest(result.getRequest());
		printController(result.getController());
		
		if (result.mapOnly()) {
			return;
		}

		printResolvedException(result.getResolvedException());
		printModelAndView(result.getModelAndView());
		printResponse(result.getResponse());

		System.out.println();
	}

	private void printRequest(MockHttpServletRequest request) {
		System.out.println("Performed " + request.getMethod() + " " + request.getRequestURI());
		printValue("Params", getParams(request));
		printValue("Headers", RequestResultMatchers.getHeaderValueMap(request));
	}
	
	private Map<String, Object> getParams(MockHttpServletRequest request) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement(); 
			String[] values = request.getParameterValues(name);
			result.put(name, (values != null) ? Arrays.asList(values) : null);
		}
		return result;
	}

	private void printValue(String label, Object value) {
		String line = getPaddedLabel(label).append(" = ").append(value).toString();
		System.out.println(line);
	}

	private StringBuilder getPaddedLabel(String label) {
		StringBuilder sb = new StringBuilder(label);
		while (sb.length() < LABEL_WIDTH) {
			sb.insert(0, " ");
		}
		return sb;
	}

	private void printController(Object handler) {
		if (handler == null) {
			System.out.println("No matching controller was found.");
		}
		else {
			System.out.println("This controller was selected: ");
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
	
	private void printResolvedException(Exception resolvedException) {
		if (resolvedException == null) {
			System.out.println("No exception was raised.");
		}
		else {
			System.out.println("The controller raised this exception, which was then successfully handled:");
			System.out.println(resolvedException);
		}
	}

	private void printModelAndView(ModelAndView mav) {
		if (mav == null) {
			System.out.println("View resolution was not required.");
		}
		else {
			System.out.println("The controller made this view selection: ");
			printValue("View", mav.isReference() ? mav.getViewName() : mav.getView());
			if (mav.getModel().size() == 0) {
				printValue("Attributes", "none");
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

	private void printResponse(MockHttpServletResponse response) {
		System.out.println("These are details of the ServletResponse: ");
		printValue("status", response.getStatus());
		printValue("error message", response.getErrorMessage());
		printValue("headers", ResponseResultMatchers.getHeaderValueMap(response));
		printValue("content type", response.getContentType());
		printValue("body", getBody(response));
		printValue("forwarded URL", response.getForwardedUrl());
		printValue("redirected URL", response.getRedirectedUrl());
		printValue("included URLs", response.getIncludedUrls());
		printValue("cookies", ResponseResultMatchers.getCookieValueMap(response));
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
