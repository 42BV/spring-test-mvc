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

package org.springframework.test.web.server.samples.standalone;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Tests using <a href="http://code.google.com/p/json-path"/>Java JsonPath</a>.
 *
 * @author Rossen Stoyanchev
 */
public class JsonResponseContentTests {

	@Test
	public void jsonPathExists() throws Exception {
		
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_JSON))
				.andExpect(response().status().isOk())
				.andExpect(response().contentType(MediaType.APPLICATION_JSON))
				.andExpect(response().content().jsonPath("$.composers[?(@.name = 'Robert Schumann')]").exists())
				.andExpect(response().content().jsonPath("$.performers[?(@.name = 'Yehudi Menuhin')]").exists());
	}

	@Test
	public void jsonPathDoesNotExist() throws Exception {
		
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_JSON))
				.andExpect(response().content().jsonPath("$.composers[10]").doesNotExist());
	}

	@Test
	public void jsonPathEvaluatesTo() throws Exception {

		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_JSON))
				.andExpect(response().status().isOk())
				.andExpect(response().contentType(MediaType.APPLICATION_JSON))
				.andExpect(response().content().jsonPath("$.composers[0].name").evaluatesTo("Johann Sebastian Bach"))
				.andExpect(response().content().jsonPath("$.performers[1].name").evaluatesTo("Yehudi Menuhin"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void jsonPath() throws Exception {

		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_JSON))
				.andExpect(response().status().isOk())
				.andExpect(response().contentType(MediaType.APPLICATION_JSON))

				.andExpect(response().content().jsonPath("$.composers").result(hasSize(4)))
				.andExpect(response().content().jsonPath("$.performers").result(hasSize(equalTo(2))))
				.andExpect(response().content().jsonPath("$.composers[?(@.name = 'Mozart')]").result(empty()))
				.andExpect(response().content().jsonPath("$.composers[0].name").result(startsWith("Johann")))
				.andExpect(response().content().jsonPath("$.performers[0].name").result(endsWith("Ashkenazy")))
				.andExpect(response().content().jsonPath("$.performers[1].name").result(containsString("di Me")))
				
				.andExpect(response().content().jsonPath("$.performers[*].name")
						.result(containsInAnyOrder("Yehudi Menuhin", "Vladimir Ashkenazy")))
							
				.andExpect(response().content().jsonPath("$.composers[*].name")
						.result(containsInAnyOrder(endsWith("Brahms"), endsWith("Grieg"), endsWith("Schumann"), endsWith("Bach"))))
							
				.andExpect(response().content().jsonPath("$.composers[1].name")
						.result(isIn(Arrays.asList("Johann Sebastian Bach", "Johannes Brahms"))));
	}

	
	@Controller
	@SuppressWarnings("unused")
	private class MusicController {

		@RequestMapping(value="/music/people")
		public @ResponseBody MultiValueMap<String, Person> get() {
			MultiValueMap<String, Person> map = new LinkedMultiValueMap<String, Person>();

			map.add("composers", new Person("Johann Sebastian Bach"));
			map.add("composers", new Person("Johannes Brahms"));
			map.add("composers", new Person("Edvard Grieg"));
			map.add("composers", new Person("Robert Schumann"));
			
			map.add("performers", new Person("Vladimir Ashkenazy"));
			map.add("performers", new Person("Yehudi Menuhin"));
			
			return map;
		}
	}
	
}
