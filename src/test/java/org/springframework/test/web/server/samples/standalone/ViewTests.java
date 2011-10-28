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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.model;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

/**
 * Tests with different View technologies.
 * 
 * @author Rossen Stoyanchev
 */
public class ViewTests {

	@Test
	public void jsp() throws Exception {
		
		// InternalResourceViewResolver with prefix and suffix.
		// No actual rendering: forwarded URL only is recorded by MockHttpServletResponse

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/");
		viewResolver.setSuffix(".jsp");
		
		standaloneSetup(new PersonController())
			.setViewResolvers(viewResolver).build()
				.perform(get("/person/Patrick"))
					.andExpect(model().attribute("person", hasProperty("name", equalTo("Patrick"))))
					.andExpect(response().status().isOk())
					.andExpect(response().forwardedUrl("/WEB-INF/person/show.jsp"));
	}

	@Test 
	public void json() throws Exception {
		
		// Always render JSON.
		
		View view = new MappingJacksonJsonView();
		
		standaloneSetup(new PersonController())
			.setSingleView(view).build()
				.perform(get("/person/Patrick"))
					.andExpect(response().status().isOk())
					.andExpect(response().contentType(MediaType.APPLICATION_JSON))
					.andExpect(response().content().jsonPath("$.person.name").evaluatesTo("Patrick"));
	}

	@Test 
	public void xml() throws Exception {
		
		// Always render XML.
		
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Person.class);

		View view = new MarshallingView(marshaller);
		
		standaloneSetup(new PersonController())
			.setSingleView(view).build()
				.perform(get("/person/Patrick"))
					.andExpect(response().status().isOk())
					.andExpect(response().contentType(MediaType.APPLICATION_XML))
					.andExpect(response().content().xpath("/person/name/text()").evaluatesTo("Patrick"));
	}

	@Test
	public void contentNegotiation() throws Exception {
		
		// Alternate between HTML, JSON, and XML depending on the file extension
		
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Person.class);
		
		List<View> views = new ArrayList<View>();
		views.add(new MappingJacksonJsonView());
		views.add(new MarshallingView(marshaller));

		ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
		contentNegotiatingViewResolver.setDefaultContentType(MediaType.TEXT_HTML);
		contentNegotiatingViewResolver.setDefaultViews(views);
		
		MockMvc mockMvc = 
			standaloneSetup(new PersonController())
				.setViewResolvers(contentNegotiatingViewResolver, new InternalResourceViewResolver())
				.build();

		mockMvc.perform(get("/person/Patrick"))
			.andExpect(response().status().isOk())
			.andExpect(response().content().isEqualTo(""))	
			.andExpect(response().forwardedUrl("person/show"));

		mockMvc.perform(get("/person/Patrick").accept(MediaType.APPLICATION_JSON))
			.andExpect(response().status().isOk())
			.andExpect(response().contentType(MediaType.APPLICATION_JSON))
			.andExpect(response().content().jsonPath("$.person.name").evaluatesTo("Patrick"));

		mockMvc.perform(get("/person/Patrick").accept(MediaType.APPLICATION_XML))
			.andExpect(response().status().isOk())
			.andExpect(response().contentType(MediaType.APPLICATION_XML))
			.andExpect(response().content().xpath("/person/name/text()").evaluatesTo("Patrick"));
	}	

	@Test 
	public void defaultConfig() throws Exception {
		
		// InternalResourceViewResolver is configured by default
		
		standaloneSetup(new PersonController()).build()
			.perform(get("/person/Patrick"))
				.andExpect(model().attribute("person", hasProperty("name", equalTo("Patrick"))))
				.andExpect(response().status().isOk())
				.andExpect(response().forwardedUrl("person/show"));
	}
	
	
	@Controller
	@SuppressWarnings("unused")
	private static class PersonController {
		
		@RequestMapping(value="/person/{name}", method=RequestMethod.GET)
		public String show(@PathVariable String name, Model model) {
			Person person = new Person(name);
			model.addAttribute(person);
			return "person/show";
		}
	}
	
}

