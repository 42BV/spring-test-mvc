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

package org.springframework.test.web.server;

import static org.springframework.test.web.server.matcher.MvcResultMatchers.loggingMatcher;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.responseBody;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneMvcSetup;
import static org.springframework.test.web.server.matcher.HandlerMatchers.*;
import static org.springframework.test.web.AssertionErrors.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * {@link MockDispatcher} test fixture.
 *
 */
public class MockDispatcherTests {
	
	@Test
	public void exceptionHandler() {
		MockMvc mockMvc = standaloneMvcSetup(new TestController()).build();
		
		mockMvc.get("/exception").addParam("succeed", "true").execute()
			.andExpect(status(200))
			.andExpect(responseBody("Ok"));
		
		mockMvc.get("/exception").addParam("succeed", "false").execute()
			.andExpect(status(200))
			.andExpect(responseBody("Exception handled"));
	}

	@Test
	public void mapOnly() {
		MockMvc mockMvc = standaloneMvcSetup(new TestController()).build();
		
		mockMvc.setMapOnly(true)
			.get("/exception").addParam("succeed", "true").execute()
				.andExpect(status(200))
				.andExpect(handlerMethod(TestController.class, "exception", boolean.class))
				.andExpect(new MvcResultMatcher() {
					public void match(MockHttpServletRequest rq, MockHttpServletResponse rs, Object h, Exception e, ModelAndView mav) {
						assertTrue("ModelAndView should be null", mav == null);
					}
				});

		mockMvc.setMapOnly(false)
			.get("/exception").addParam("succeed", "true").execute()
				.andExpect(status(200))
				.andExpect(loggingMatcher())
				.andExpect(responseBody("Ok"));
	}
	
	@SuppressWarnings("unused")
	@Controller
	private static class TestController {

		@RequestMapping("/exception")
		public @ResponseBody String exception(boolean succeed) {
			if (succeed) {
				return "Ok";
			}
			else {
				throw new IllegalStateException("Sorry");
			}
		}

		@ExceptionHandler
		public @ResponseBody String handle(IllegalStateException e) {
			return "Exception handled";
		}
	}

}
