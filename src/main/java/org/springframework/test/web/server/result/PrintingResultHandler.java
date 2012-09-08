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

package org.springframework.test.web.server.result;

import java.io.OutputStream;
import java.io.PrintStream;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultHandler;
import org.springframework.test.web.support.PrintStreamValuePrinter;
import org.springframework.test.web.support.ValuePrinter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * A {@code ResultHandler} that writes request and response details to an
 * {@link OutputStream}. Use {@link MockMvcResultHandlers#print()} to get access
 * to an instance that writes to {@code System#out}.
 *
 * @author Rossen Stoyanchev
 */
public class PrintingResultHandler implements ResultHandler {

	private final OutputStream out;


	/**
	 * Protected class constructor.
	 * @see MockMvcResultHandlers#print()
	 */
	protected PrintingResultHandler(OutputStream out) {
		this.out = out;
	}

	public final void handle(MvcResult mvcResult) throws Exception {

		String encoding = mvcResult.getResponse().getCharacterEncoding();

		PrintStream printStream = new PrintStream(this.out, true,
				(encoding != null) ? encoding : WebUtils.DEFAULT_CHARACTER_ENCODING);

		ValuePrinter printer = createValuePrinter(printStream);

		printer.printHeading("MockHttpServletRequest");
		printRequest(mvcResult.getRequest(), printer);

		printer.printHeading("Handler");
		printHandler(mvcResult.getHandler(), mvcResult.getInterceptors(), printer);

		printer.printHeading("Resolved Exception");
		printResolvedException(mvcResult.getResolvedException(), printer);

		printer.printHeading("ModelAndView");
		printModelAndView(mvcResult.getModelAndView(), printer);

		printer.printHeading("FlashMap");
		printFlashMap(RequestContextUtils.getOutputFlashMap(mvcResult.getRequest()), printer);

		printer.printHeading("MockHttpServletResponse");
		printResponse(mvcResult.getResponse(), printer);
	}

	/**
	 * Create the ValuePrinter instance to use for printing.
	 */
	protected ValuePrinter createValuePrinter(PrintStream printStream) {
		return new PrintStreamValuePrinter(printStream);
	}

	/**
	 * Print the request.
	 */
	protected void printRequest(MockHttpServletRequest request, ValuePrinter printer) throws Exception {
		printer.printValue("HTTP Method", request.getMethod());
		printer.printValue("Request URI", request.getRequestURI());
		printer.printValue("Parameters", request.getParameterMap());
		printer.printValue("Headers", ResultHandlerUtils.getRequestHeaderMap(request));
	}

	/**
	 * Print the handler.
	 */
	protected void printHandler(Object handler, HandlerInterceptor[] interceptors, ValuePrinter printer) throws Exception {
		if (handler == null) {
			printer.printValue("Type", null);
		}
		else {
			if (handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				printer.printValue("Type", handlerMethod.getBeanType().getName());
				printer.printValue("Method", handlerMethod);
			}
			else {
				printer.printValue("Type", handler.getClass().getName());
			}
		}
	}

	/**
	 * Print exceptions resolved through a HandlerExceptionResolver.
	 */
	protected void printResolvedException(Exception resolvedException, ValuePrinter printer) throws Exception {
		if (resolvedException == null) {
			printer.printValue("Type", null);
		}
		else {
			printer.printValue("Type", resolvedException.getClass().getName());
		}
	}

	/**
	 * Print the ModelAndView.
	 */
	protected void printModelAndView(ModelAndView mav, ValuePrinter printer) throws Exception {
		printer.printValue("View name", (mav != null) ? mav.getViewName() : null);
		printer.printValue("View", (mav != null) ? mav.getView() : null);
		if (mav == null || mav.getModel().size() == 0) {
			printer.printValue("Model", null);
		}
		else {
			for (String name : mav.getModel().keySet()) {
				if (!name.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
					Object value = mav.getModel().get(name);
					printer.printValue("Attribute", name);
					printer.printValue("value", value);
					Errors errors = (Errors) mav.getModel().get(BindingResult.MODEL_KEY_PREFIX + name);
					if (errors != null) {
						printer.printValue("errors", errors.getAllErrors());
					}
				}
			}
		}
	}

	/**
	 * Print output flash attributes.
	 */
	protected void printFlashMap(FlashMap flashMap, ValuePrinter printer) throws Exception {
		if (flashMap == null) {
			printer.printValue("Attributes", null);
		}
		else {
			for (String name : flashMap.keySet()) {
				printer.printValue("Attribute", name);
				printer.printValue("value", flashMap.get(name));
			}
		}
	}

	/**
	 * Print the response.
	 */
	protected void printResponse(MockHttpServletResponse response, ValuePrinter printer) throws Exception {
		printer.printValue("Status", response.getStatus());
		printer.printValue("Error message", response.getErrorMessage());
		printer.printValue("Headers", ResultHandlerUtils.getResponseHeaderMap(response));
		printer.printValue("Content type", response.getContentType());
		printer.printValue("Body", response.getContentAsString());
		printer.printValue("Forwarded URL", response.getForwardedUrl());
		printer.printValue("Redirected URL", response.getRedirectedUrl());
		printer.printValue("Cookies", response.getCookies());
	}

}
