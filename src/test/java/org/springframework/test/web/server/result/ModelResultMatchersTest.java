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

import org.junit.Test;
import org.springframework.test.web.server.StubMvcResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Craig Walls
 */
public class ModelResultMatchersTest {

	@Test
	public void attributeExists() throws Exception {
		new ModelResultMatchers().attributeExists("good").match(getStubMvcResult());
	}

	@Test(expected=AssertionError.class)
	public void attributeExists_doesntExist() throws Exception {
		new ModelResultMatchers().attributeExists("bad").match(getStubMvcResult());
	}

	private StubMvcResult getStubMvcResult() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("good", "good");
		StubMvcResult mvcResult = new StubMvcResult(null, null, null, null, modelAndView, null, null);
		return mvcResult;
	}

}
