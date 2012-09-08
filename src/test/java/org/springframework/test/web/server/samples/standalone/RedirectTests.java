/*
 * Copyright 2011 the original author or authors.
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

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import javax.validation.Valid;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.Person;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Redirect scenarios.
 *
 * @author Rossen Stoyanchev
 */
public class RedirectTests {

	@Test
	public void testRedirect() throws Exception {
		standaloneSetup(new PersonController()).build()
			.perform(post("/persons").param("name", "Andy"))
				.andExpect(status().isOk())
	            .andExpect(redirectedUrl("/person/1"))
	            .andExpect(model().size(1))
	            .andExpect(model().attributeExists("id"))
	            .andExpect(flash().attributeCount(1))
	            .andExpect(flash().attribute("message", "success!"));
	}

	@Test
	public void testBindingErrors() throws Exception {
		standaloneSetup(new PersonController()).build()
			.perform(post("/persons"))
				.andExpect(status().isOk())
	            .andExpect(forwardedUrl("person/add"))
	            .andExpect(model().size(1))
	            .andExpect(model().attributeExists("person"))
	            .andExpect(flash().attributeCount(0));
	}

	
	@Controller
	@SuppressWarnings("unused")
	private static class PersonController {
		
		@RequestMapping(value="/persons", method=RequestMethod.POST)
		public String save(@Valid Person person, Errors errors, RedirectAttributes redirectAttrs) {
			if (errors.hasErrors()) {
				return "person/add";
			}
			redirectAttrs.addAttribute("id", "1");
			redirectAttrs.addFlashAttribute("message", "success!");
			return "redirect:/person/{id}";
		}
	}
}
