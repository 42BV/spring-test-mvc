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

package org.springframework.test.web.server.samples.context;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.annotationConfigSetup;
import static org.springframework.test.web.server.setup.MockMvcBuilders.xmlConfigSetup;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

/**
 * Tests that need to have the web application root configured to allow access
 * to web application resources -- e.g. serving .js and .css files, loading
 * Tiles definitions, etc.
 *
 * @author Rossen Stoyanchev
 */
public class WarRootDirectoryTests {

	private static MockMvc mockMvc;

	@BeforeClass
	public static void setup() {

		// Indicate where the webapp root is located.
		// That can be classpath or JVM-relative (e.g. "src/main/webapp").

		String warRootDir = "src/test/resources/META-INF/web-resources";
		boolean isClasspathRelative = false;

		// Use this flag to switch between Java and XML-based configuration
		boolean useJavaConfig = true;

		if (useJavaConfig) {
			mockMvc =
				annotationConfigSetup(WebConfig.class)
					.configureWebAppRootDir(warRootDir, isClasspathRelative)
					.build();
		}
		else {
			mockMvc =
				xmlConfigSetup("classpath:org/springframework/test/web/server/samples/servlet-context.xml")
					.configureWebAppRootDir(warRootDir, isClasspathRelative)
					.build();
		}
	}

	// Tiles definitions (i.e. TilesConfigurer -> "/WEB-INF/**/tiles.xml").

	@Test
	public void tilesDefinitions() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

	// Resource request (i.e. <mvc:resources ... />).

	@Test
	public void resourceRequest() throws Exception {
		mockMvc.perform(get("/resources/Spring.js"))
			.andExpect(status().isOk())
			.andExpect(content().type("text/javascript"))
			.andExpect(content().string(containsString("Spring={};")));
	}

	// Resource request forwarded to the default servlet (i.e. <mvc:default-servlet-handler />).

	@Test
	public void resourcesViaDefaultServlet() throws Exception {
		mockMvc.perform(get("/unknown/resource"))
			.andExpect(status().isOk())
			.andExpect(handler().type(DefaultServletHttpRequestHandler.class))
			.andExpect(forwardedUrl("default"));
	}

}
