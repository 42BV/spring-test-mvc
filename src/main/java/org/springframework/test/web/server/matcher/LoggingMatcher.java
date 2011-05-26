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

package org.springframework.test.web.server.matcher;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MvcResultMatcher;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

public class LoggingMatcher implements MvcResultMatcher {

	private static final Log logger = LogFactory.getLog(LoggingMatcher.class);

	public void match(MockHttpServletRequest request, 
					  MockHttpServletResponse response, 
					  Object handler, 
					  Exception handlerException,
					  ModelAndView mav) {
		
		StringBuilder sb = new StringBuilder();
		
		appendRequest(sb, request);
		appendHandler(sb, handler, handlerException);
		appendModelAndView(sb, mav);
		appendResponse(sb, response);

		logger.info(sb.toString());
	}

	private void appendRequest(StringBuilder sb, MockHttpServletRequest request) {
		sb.append("\n\n" + request.getMethod() + " " + request.getRequestURI() + "\n");
		appendLabelAndValue(sb, "Params", request.getParameterMap());
		appendLabelAndValue(sb, "Headers", MockRequestMatchers.getHeaderValueMap(request));
	}

	private void appendHandler(StringBuilder sb, Object handler, Exception handlerException) {
		if (handler == null) {
			sb.append("\nSelected Handler: null\n");
			return;
		}
		
		sb.append("\nSelected Handler:\n");
		if (!HandlerMethod.class.isInstance(handler)) {
			appendLabelAndValue(sb, "Type", handler.getClass().getName());
			appendLabelAndValue(sb, "Method", "Not available");
		}
		else {
			HandlerMethod hm = (HandlerMethod) handler;
			appendLabelAndValue(sb, "Type", hm.getBeanType().getName());
			appendLabel(sb, "Method");
			
			sb.append(hm.getReturnType().getParameterType().getSimpleName());
			sb.append(" " + hm.getMethod().getName() + "(");
			for (int i = 0; i < hm.getMethod().getParameterTypes().length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(hm.getMethod().getParameterTypes()[i].getSimpleName());
			}
			sb.append(") \n");
		}
		
		if (handlerException == null) {
			sb.append("\nHandler Exception Raised: none\n");
		}
		else {
			sb.append("\nHandler Exception Raised:\n" + handlerException + "\n");
		}
	}

	private void appendLabel(StringBuilder sb, String label) {
		for (int i = 0; i < (17 - label.length()); i++) {
			sb.append(" ");
		}
		sb.append(label + ": ");
	}

	private void appendLabelAndValue(StringBuilder sb, String label, Object value) {
		appendLabel(sb, label);
		sb.append(value + "\n");
	}

	private void appendModelAndView(StringBuilder sb, ModelAndView mav) {
		sb.append("\nModelAndView: ");
		if (mav != null) {
			sb.append("\n");
			appendView(sb, mav);
			appendModel(sb, mav.getModel());
		}
		else {
			sb.append("null\n");
		}
	}

	private void appendView(StringBuilder sb, ModelAndView mav) {
		Assert.notNull(mav);
		if (mav.isReference()) {
			appendLabelAndValue(sb, "View name", "\"" + mav.getViewName() + "\"");
		}
		else {
			appendLabelAndValue(sb, "View", mav.getView());
		}
	}

	private void appendModel(StringBuilder sb, Map<String, Object> model) {
		if (model.size() == 0) {
			appendLabelAndValue(sb, "Attributes", "none");
			sb.append("none");
			return;
		}
		for (String name : model.keySet()) {
			if (!name.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
				Object value = model.get(name);
				Errors errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + name);
				if (errors == null) {
					appendLabelAndValue(sb, "Attribute", name);
				}
				else {
					appendLabelAndValue(sb, "Attribute", name + " has " + errors.getErrorCount() + " errors");
				}
				if (logger.isTraceEnabled()) {
					appendLabelAndValue(sb, "value", value);
					if (errors != null) {
						appendLabelAndValue(sb, "errors", errors.getAllErrors());
					}
				}
			}
		}
	}

	private void appendResponse(StringBuilder sb, MockHttpServletResponse response) {
		sb.append("\nResponse:\n");
		appendLabelAndValue(sb, "status", response.getStatus());
		appendLabelAndValue(sb, "error message", response.getErrorMessage());
		appendLabelAndValue(sb, "headers", MockResponseMatchers.getHeaderValueMap(response));
		appendLabelAndValue(sb, "content type", response.getContentType());
		appendResponseBody(sb, response);
		appendLabelAndValue(sb, "forwarded URL", response.getForwardedUrl());
		appendLabelAndValue(sb, "redirected URL", response.getRedirectedUrl());
		appendLabelAndValue(sb, "included URLs", response.getIncludedUrls());
		appendLabelAndValue(sb, "cookies", MockResponseMatchers.getCookieValueMap(response));
		sb.append("\n");
	}

	private void appendResponseBody(StringBuilder sb, MockHttpServletResponse response) {
		String content;
		try {
			content = response.getContentAsString();
			
		} catch (UnsupportedEncodingException e) {
			String message = "Failed to get the response content: ";
			content = message + e.toString();
			logger.error(message, e);
		}
		if (content != null) {
			int length = content.length();
			if (length > 50) {
				content = content.substring(0, 50);
				appendLabelAndValue(sb, "response body", "[" + content + "] <trunkated> (50 of " + " " + length + " chars)");
			}
			else {
				appendLabelAndValue(sb, "response body", "[" + content + "]");
			}
		}
	}

}
