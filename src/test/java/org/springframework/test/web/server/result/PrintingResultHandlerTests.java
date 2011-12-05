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

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.StubMvcResult;
import org.springframework.test.web.support.ValuePrinter;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO ...
 *
 * @author Rossen Stoyanchev
 */
public class PrintingResultHandlerTests {

	private TestValuePrinter printer;
	private PrintingResultHandler handler;
	
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private StubMvcResult mvcResult;
	
	@Before
	public void setup() {
		this.printer = new TestValuePrinter();
		this.handler = new TestPrintingResultHandler(System.out, this.printer);
		this.request = new MockHttpServletRequest("GET", "/");
		this.response = new MockHttpServletResponse();
		this.mvcResult = new StubMvcResult(this.request, null, null, null, null, null, this.response);
	}

	@Test
	public void testPrintRequest() throws Exception {
		this.request.addParameter("param", "paramValue");
		this.request.addHeader("header", "headerValue");
		
		this.handler.handle(this.mvcResult);
		
		String heading = "MockHttpServletRequest";
		assertValue(heading, "HTTP Method", this.request.getMethod());
		assertValue(heading, "Request URI", this.request.getRequestURI());
		assertValue(heading, "Parameters", this.request.getParameterMap());
		assertValue(heading, "Headers", ResultHandlerUtils.getRequestHeaderMap(this.request));
	}
	
	@Test
	public void testPrintResponse() throws Exception {
		this.response.setStatus(400, "error");
		this.response.addHeader("header", "headerValue");
		this.response.setContentType("text/plain");
		this.response.getWriter().print("content");
		this.response.setForwardedUrl("redirectFoo");
		this.response.sendRedirect("/redirectFoo");
		this.response.addCookie(new Cookie("cookie", "cookieValue"));
		
		this.handler.handle(this.mvcResult);
		
		String heading = "MockHttpServletResponse";
		assertValue(heading, "Status", this.response.getStatus());
		assertValue(heading, "Error message", response.getErrorMessage());
		assertValue(heading, "Headers", ResultHandlerUtils.getResponseHeaderMap(this.response));
		assertValue(heading, "Content type", this.response.getContentType());
		assertValue(heading, "Body", this.response.getContentAsString());
		assertValue(heading, "Forwarded URL", this.response.getForwardedUrl());
		assertValue(heading, "Redirected URL", this.response.getRedirectedUrl());
	}

	@Test
	public void testPrintHandlerNull() throws Exception {
		StubMvcResult mvcResult = new StubMvcResult(this.request, null, null, null, null, null, this.response);
		this.handler.handle(mvcResult);

		String heading = "Handler";
		assertValue(heading, "Type", null);
	}
	
	@Test
	public void testPrintHandler() throws Exception {
		this.mvcResult.setHandler(new Object());
		this.handler.handle(this.mvcResult);

		String heading = "Handler";
		assertValue(heading, "Type", Object.class.getName());
	}
	
	@Test
	public void testPrintHandlerMethod() throws Exception {
		HandlerMethod handlerMethod = new HandlerMethod(this, "handle");
		this.mvcResult.setHandler(handlerMethod);
		this.handler.handle(mvcResult);

		String heading = "Handler";
		assertValue(heading, "Type", this.getClass().getName());
		assertValue(heading, "Method", handlerMethod);
	}

	@Test
	public void testResolvedExceptionNull() throws Exception {
		this.handler.handle(this.mvcResult);

		String heading = "Resolved Exception";
		assertValue(heading, "Type", null);
	}

	@Test
	public void testResolvedException() throws Exception {
		this.mvcResult.setResolvedException(new Exception());
		this.handler.handle(this.mvcResult);


		String heading = "Resolved Exception";
		assertValue(heading, "Type", Exception.class.getName());
	}

	@Test
	public void testModelAndViewNull() throws Exception {
		this.handler.handle(this.mvcResult);

		String heading = "ModelAndView";
		assertValue(heading, "View name", null);
		assertValue(heading, "View", null);
		assertValue(heading, "Model", null);
	}

	@Test
	public void testModelAndView() throws Exception {
		BindException bindException = new BindException(new Object(), "target");
		bindException.reject("errorCode");
		
		ModelAndView mav = new ModelAndView("viewName");
		mav.addObject("attrName", "attrValue");
		mav.addObject(BindingResult.MODEL_KEY_PREFIX + "attrName", bindException);

		this.mvcResult.setMav(mav);
		this.handler.handle(this.mvcResult);

		String heading = "ModelAndView";
		assertValue(heading, "View name", "viewName");
		assertValue(heading, "View", null);
		assertValue(heading, "Attribute", "attrName");
		assertValue(heading, "value", "attrValue");
		assertValue(heading, "errors", bindException.getAllErrors());
	}

	@Test
	public void testFlashMapNull() throws Exception {
		this.handler.handle(mvcResult);

		String heading = "FlashMap";
		assertValue(heading, "Type", null);
	}

	@Test
	public void testFlashMap() throws Exception {
		FlashMap flashMap = new FlashMap();
		flashMap.put("attrName", "attrValue");
		this.request.setAttribute(FlashMapManager.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
		
		this.handler.handle(this.mvcResult);

		String heading = "FlashMap";
		assertValue(heading, "Attribute", "attrName");
		assertValue(heading, "value", "attrValue");
	}

	
	private void assertValue(String heading, String label, Object value) {
		assertTrue("Heading " + heading + " not printed", this.printer.values.containsKey(heading));
		assertEquals(value, this.printer.values.get(heading).get(label));
	}

	
	private static class TestPrintingResultHandler extends PrintingResultHandler {

		private final ValuePrinter printer;
		
		public TestPrintingResultHandler(OutputStream out, TestValuePrinter printer) {
			super(out);
			this.printer = printer;
		}
		
		@Override
		protected ValuePrinter createValuePrinter(PrintStream printStream) {
			return this.printer;
		}		
	}
	
	private static class TestValuePrinter implements ValuePrinter {
		
		private String currentHeading;
		
		private final Map<String, Map<String, Object>> values = new HashMap<String, Map<String, Object>>();
		
 		public void printHeading(String heading) {
 			this.currentHeading = heading;
 			this.values.put(heading, new HashMap<String, Object>());
		}

		public void printValue(String label, Object value) {
			Assert.notNull(this.currentHeading, "Heading not printed before label " + label + " with value " + value);
			this.values.get(this.currentHeading).put(label, value);
		}
	}

	public void handle() {
	}
}
