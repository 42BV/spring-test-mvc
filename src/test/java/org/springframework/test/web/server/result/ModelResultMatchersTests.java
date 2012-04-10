/*
 * Copyright 2002-2012 the original author or authors.
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

import static org.hamcrest.Matchers.is;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.StubMvcResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Craig Walls
 */
public class ModelResultMatchersTests {

	private ModelResultMatchers matchers;

	private ModelAndView mav;

	private ModelAndView mavWithError;

	@Before
	public void setUp() throws Exception {
		this.matchers = new ModelResultMatchers();

		this.mav = new ModelAndView("view", "good", "good");

		Date date = new Date();
		BindingResult bindingResult = new BindException(date, "date");
		bindingResult.rejectValue("time", "error");

		this.mavWithError = new ModelAndView("view", "good", "good");
		this.mavWithError.addObject("date", date);
		this.mavWithError.addObject(BindingResult.MODEL_KEY_PREFIX + "date", bindingResult);
	}

	@Test
	public void attributeExists() throws Exception {
		this.matchers.attributeExists("good").match(getMvcResult(this.mav));
	}

	@Test(expected=AssertionError.class)
	public void attributeExists_doesNotExist() throws Exception {
		this.matchers.attributeExists("bad").match(getMvcResult(this.mav));
	}

	@Test
	public void attribute_equal() throws Exception {
		this.matchers.attribute("good", is("good")).match(getMvcResult(this.mav));
	}

	@Test(expected=AssertionError.class)
	public void attribute_notEqual() throws Exception {
		this.matchers.attribute("good", is("bad")).match(getMvcResult(this.mav));
	}

	@Test
	public void hasNoErrors() throws Exception {
		this.matchers.hasNoErrors().match(getMvcResult(this.mav));
	}

	@Test(expected=AssertionError.class)
	public void hasNoErrors_withErrors() throws Exception {
		this.matchers.hasNoErrors().match(getMvcResult(this.mavWithError));
	}

	@Test
	public void attributeHasErrors() throws Exception {
		this.matchers.attributeHasErrors("date").match(getMvcResult(this.mavWithError));
	}

	@Test(expected=AssertionError.class)
	public void attributeHasErrors_withoutErrors() throws Exception {
		this.matchers.attributeHasErrors("good").match(getMvcResult(this.mavWithError));
	}

	private MvcResult getMvcResult(ModelAndView modelAndView) {
		return new StubMvcResult(null, null, null, null, modelAndView, null, null);
	}
}
