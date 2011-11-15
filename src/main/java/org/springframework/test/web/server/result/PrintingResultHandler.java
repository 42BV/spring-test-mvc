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
import java.io.PrintStream;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultHandler;
import org.springframework.test.web.support.SimpleValuePrinter;
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
 * A convenient base class for ResultHandler implementations that allows sub-classes
 * to match one thing at a time -- the request, the response, etc.
 *
 * @author Rossen Stoyanchev
 */
public class PrintingResultHandler implements ResultHandler {

	private final OutputStream out;
	
	/**
	 * Class constructor
	 * @param out an OutputStream to print to
	 */
	public PrintingResultHandler(OutputStream out) {
		this.out = out;
	}

	public final void handle(MockHttpServletRequest request, 
							 MockHttpServletResponse response, 
							 Object handler,
							 HandlerInterceptor[] interceptors, 
							 ModelAndView mav, 
							 Exception resolvedException) throws Exception {

		String encoding = response.getCharacterEncoding();
		
		PrintStream printStream = new PrintStream(this.out, true, 
				(encoding != null) ? encoding : WebUtils.DEFAULT_CHARACTER_ENCODING);
		
		ValuePrinter printer = createValuePrinter(printStream);

		printer.printHeading("MockHttpServletRequest");
		printRequest(request, printer);

		printer.printHeading("Handler");
		printHandler(handler, interceptors, printer);

		printer.printHeading("Resolved Exception");
		printResolvedException(resolvedException, printer);

		printer.printHeading("ModelAndView");
		printModelAndView(mav, printer);

		printer.printHeading("FlashMap");
		printFlashMap(RequestContextUtils.getOutputFlashMap(request), printer);

		printer.printHeading("MockHttpServletResponse");
		printResponse(response, printer);
	}

	/**
	 * Create the ValuePrinter instance to use for printing.
	 */
	protected ValuePrinter createValuePrinter(PrintStream printStream) {
		return new SimpleValuePrinter(printStream);
	}
	
	/**
	 * Prints the request.
	 * @param request the request
	 * @param printer a PrintStream matching the character encoding of the response.a PrintStream matching the character encoding of the response. 
	 */
	protected void printRequest(MockHttpServletRequest request, ValuePrinter printer) throws Exception {
		printer.printValue("HTTP Method", request.getMethod());
		printer.printValue("Request URI", request.getRequestURI());
		printer.printValue("Parameters", request.getParameterMap());
		printer.printValue("Headers", ResultHandlerUtils.getRequestHeaderMap(request));
	}

	/**
	 * Prints the handler.
	 * @param handler the selected handler
	 * @param interceptors the selected interceptors
	 * @param printer a ResponsePrinter matching the character encoding of the response.
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
	 * Prints exceptions resolved through a HandlerExceptionResolver. 
	 * @param resolvedException the resolved exception
	 * @param printer a ResponsePrinter matching the character encoding of the response.
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
	 * Prints the model and the view.
	 * @param mav the model and view produced
	 * @param printer a ResponsePrinter matching the character encoding of the response.
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
	 * Prints output flash attributes.
	 * @param flashMap the output FlashMap
	 * @param printer a ResponsePrinter matching the character encoding of the response.
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
	 * Prints the response.
	 * @param response the response
	 * @param printer a ResponsePrinter matching the character encoding of the response.
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
