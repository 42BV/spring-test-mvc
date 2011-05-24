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

import static org.springframework.test.web.server.matcher.MvcResultMatchers.contentType;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.responseBody;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MvcServerBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvcServer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

public class StandaloneSetupViewResolverTests {

	@Test
	public void internalResourceViewResolver() throws Exception {

		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(InternalResourceView.class);
		
		MockMvcServer server = standaloneSetup(new TestController())
			.setViewResolvers(resolver)
			.buildServer();
		
		server.get("/path")
			.execute()
				.andExpect(status(200))
				.andExpect(forwardedUrl("fruitsAndVegetables"));
	}

	@Test 
	public void fixedViewResolver() throws Exception {
		
		View view = new MappingJacksonJsonView();
		
		MockMvcServer server = standaloneSetup(new TestController())
			.configureFixedViewResolver(view)
			.buildServer();
		
		server.get("/path")
			.execute()
				.andExpect(status(200))
				.andExpect(contentType("application/json"))
				.andExpect(responseBody("{\"vegetable\":\"cucumber\",\"fruit\":\"kiwi\"}"));
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
		
		MockMvcServer server = standaloneSetup(new TestController())
			.setViewResolvers(viewResolver, internalResourceViewResolver)
			.buildServer();

		server.get("/path.json")
			.execute()
				.andExpect(status(200))
				.andExpect(contentType("application/json"))
				.andExpect(responseBody("{\"vegetable\":\"cucumber\",\"fruit\":\"kiwi\"}"));

		server.get("/path.xml")
			.execute()
				.andExpect(status(200))
				.andExpect(contentType("application/xml"))
				.andExpect(responseBody("<string>cucumber</string>"));	// First attribute
		
		server.get("/path")
			.execute()
				.andExpect(status(200))
				.andExpect(forwardedUrl("fruitsAndVegetables"));
	}

	@Controller
	public class TestController {

		@RequestMapping("/path")
		public String handle(Model model) {
			model.addAttribute("fruit", "kiwi");
			model.addAttribute("vegetable", "cucumber");
			return "fruitsAndVegetables";
		}
	}
}
