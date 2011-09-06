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

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.console;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneMvcSetup;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Test fixture for {@link MockDispatcher} tests.
 */
public class MockDispatcherTests {
	
	@Test
	public void exceptionHandler() {
		MockMvc mockMvc = standaloneMvcSetup(new TestController()).build();
		
		mockMvc.perform(get("/exception").param("succeed", "true"))
                .andExpect(response().status(HttpStatus.OK))
                .andExpect(response().body("Ok")).andPrintTo(console());
		
		mockMvc.perform(get("/exception").param("succeed", "false"))
			.andExpect(response().status(HttpStatus.OK))
			.andExpect(response().body("Exception handled"));
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
