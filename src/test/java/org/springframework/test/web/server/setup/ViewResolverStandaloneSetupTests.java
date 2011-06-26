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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

import org.junit.Test;

import static org.springframework.test.web.server.request.MockHttpServletRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneMvcSetup;

/**
 * Scenarios for setting up view resolution with a {@link StandaloneMockMvcBuilder}.
 * 
 */
public class ViewResolverStandaloneSetupTests {

	@Test
	public void internalResourceViewResolver() throws Exception {

		standaloneMvcSetup(new TestController())
			.setViewResolvers(new InternalResourceViewResolver()).build()
				.perform(get("/path"))
					.andExpect(response().status(200))
					.andExpect(response().forwardedUrl("fruitsAndVegetables"));
	}

	@Test 
	public void fixedViewResolver() throws Exception {
		
		standaloneMvcSetup(new TestController())
			.configureFixedViewResolver(new MappingJacksonJsonView()).build()
				.perform(get("/path"))
					.andExpect(response().status(200))
					.andExpect(response().contentType("application/json"))
					.andExpect(response().body("{\"vegetable\":\"cucumber\",\"fruit\":\"kiwi\"}"));
	}
	
	@Test
	public void contentNegotiatingViewResolver() throws Exception {
		
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setViewClass(InternalResourceView.class);

		List<View> views = new ArrayList<View>();
		views.add(new MappingJacksonJsonView());
		views.add(new MarshallingView(new XStreamMarshaller()));

		Map<String, String> mediaTypes = new HashMap<String, String>();
		mediaTypes.put("json", "application/json");
		mediaTypes.put("xml", "application/xml");

		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
		viewResolver.setDefaultViews(views);
		viewResolver.setMediaTypes(mediaTypes);
		viewResolver.setDefaultContentType(MediaType.TEXT_HTML);
		
		MockMvc mockMvc = standaloneMvcSetup(new TestController())
			.setViewResolvers(viewResolver, internalResourceViewResolver)
			.build();

		mockMvc.perform(get("/path.json"))
				.andExpect(response().status(200))
				.andExpect(response().contentType("application/json"))
				.andExpect(response().body("{\"vegetable\":\"cucumber\",\"fruit\":\"kiwi\"}"));

		mockMvc.perform(get("/path.xml"))
				.andExpect(response().status(200))
				.andExpect(response().contentType("application/xml"))
				.andExpect(response().body("<string>cucumber</string>"));	// First attribute
		
		mockMvc.perform(get("/path"))
				.andExpect(response().status(200))
				.andExpect(response().forwardedUrl("fruitsAndVegetables"));
	}

	@Controller
	class TestController {

		@RequestMapping("/path")
		public String handle(Model model) {
			model.addAttribute("fruit", "kiwi");
			model.addAttribute("vegetable", "cucumber");
			return "fruitsAndVegetables";
		}
	}
}
