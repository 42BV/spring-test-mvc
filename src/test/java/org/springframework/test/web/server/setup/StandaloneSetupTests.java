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

package org.springframework.test.web.server.setup;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneMvcSetup;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class StandaloneSetupTests {

	@Test
	public void singleController() throws Exception {
		
		standaloneMvcSetup(new TestController()).build()
			.perform(get("/path"))
				.andExpect(response().status().is(HttpStatus.OK))
				.andExpect(response().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(response().body("Mapped by path!"));
	}	

	@Controller
	class TestController {

		@RequestMapping("/path")
		public @ResponseBody String handle() {
			return "Mapped by path!";
		}
	}

}
