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

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.StubMvcResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

/**
 * @author Craig Walls
 */
public class ModelResultMatchersTests {

	private ModelResultMatchers matchers;

	@Before
	public void setUp() throws Exception {
		matchers = new ModelResultMatchers();
	}

	@Test
	public void attributeExists() throws Exception {
		match(matchers.attributeExists("good"), goodResult());
	}

	@Test(expected=AssertionError.class)
	public void attributeExists_doesNotExist() throws Exception {
		match(matchers.attributeExists("bad"), goodResult());
	}

	@Test
	public void attribute_equal() throws Exception {
		match(matchers.attribute("good", is("good")), goodResult());
	}

	@Test(expected=AssertionError.class)
	public void attribute_notEqual() throws Exception {
		match(matchers.attribute("good", is("bad")), goodResult());
	}

	@Test
	public void hasNoErrors() throws Exception {
		match(matchers.hasNoErrors(), goodResult());
	}

	@Test(expected=AssertionError.class)
	public void hasNoErrors_withErrors() throws Exception {
		match(matchers.hasNoErrors(), errorResult());
	}

	@Test
	public void attributeHasErrors() throws Exception {
		match(matchers.attributeHasErrors("date"), errorResult());
	}

	@Test(expected=AssertionError.class)
	public void attributeHasErrors_withoutErrors() throws Exception {
		match(matchers.attributeHasErrors("good"), errorResult());
	}

	@Test
	public void attributeFieldError_hasProperty() throws Exception {
		match(matchers.attributeErrors("date", hasItem(hasProperty("field", is("time")))), errorResult());
		match(matchers.attributeErrors("date", hasItem(hasProperty("objectName", is("date")))), errorResult());
	}

	@Test(expected=AssertionError.class)
	public void attributeFieldError_doesNotHaveProperty() throws Exception {
		match(matchers.attributeErrors("date", hasItem(hasProperty("objectName", is("bad")))), errorResult());
	}

	private void match(ResultMatcher matcher, MvcResult mvcResult) throws Exception {
		matcher.match(mvcResult);
	}

	private MvcResult goodResult() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("good", "good");
		return getStubMvcResult(modelAndView);
	}

	private MvcResult errorResult() {
		Date date = new Date();

		BindingResult bindingResult = new BindException(date, "date");
		bindingResult.rejectValue("time", "error");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("good", "good");
		modelAndView.addObject("date", date);
		modelAndView.addObject(BindingResult.MODEL_KEY_PREFIX + "date", bindingResult);

		return getStubMvcResult(modelAndView);
	}

	private MvcResult getStubMvcResult(ModelAndView modelAndView) {
		return new StubMvcResult(null, null, null, null, modelAndView, null, null);
	}
}
