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

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.springframework.test.web.server.MockHttpServletRequestBuilders.get;
import static org.springframework.test.web.server.matcher.HandlerMatchers.handlerType;
import static org.springframework.test.web.server.matcher.MvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.annotationConfigMvcSetup;
import static org.springframework.test.web.server.setup.MockMvcBuilders.xmlConfigMvcSetup;

/**
 * Test access to web application resources through the MockServletContext.
 * The WAR root may be file system based or classpath-relative.
 *
 */
@RunWith(Parameterized.class)
public class WebResourceTests {

	@Parameters
	public static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[][] { 
				{ ConfigType.XML, "src/test/webapp", false }, 
				{ ConfigType.XML, "META-INF/web-resources", true },
				{ ConfigType.ANNOT, "src/test/webapp", false }, 
				{ ConfigType.ANNOT, "META-INF/web-resources", true }
		});
	}
	
	private MockMvc mockMvc;
	
	public WebResourceTests(ConfigType configType, String webResourcePath, boolean isClasspathRelative) {
		
		if (ConfigType.XML.equals(configType)) {
			String location = "classpath:org/springframework/test/web/server/setup/servlet-context.xml";
			mockMvc = xmlConfigMvcSetup(location)
				.configureWarRootDir(webResourcePath, isClasspathRelative)
				.build();
		}
		else {
			mockMvc = annotationConfigMvcSetup(TestConfiguration.class)
				.configureWarRootDir(webResourcePath, isClasspathRelative)
				.build();
		}
	}
	
	@Test
	public void testWebResources() {

		// TilesView
		mockMvc.perform(get("/form"))
                .andExpect(status(200)).andExpect(forwardedUrl("/WEB-INF/layouts/main.jsp"));

		mockMvc.perform(get("/resources/Spring.js"))
				.andExpect(status(200))
				.andExpect(handlerType(ResourceHttpRequestHandler.class))
				.andExpect(contentType("application/octet-stream"))
				.andExpect(responseBodyContains("Spring={};"));
		
		mockMvc.perform(get("/unknown/resource.js"))
			.andExpect(status(200))
			.andExpect(handlerType(DefaultServletHttpRequestHandler.class))
			.andExpect(forwardedUrl("default"));
	}
	
	@Controller
	static class TestController {

		@RequestMapping("/form")
		public void show() {
		}
	}

	@Configuration
	@EnableWebMvc
	static class TestConfiguration extends WebMvcConfigurerAdapter {

		@Override
		public void configureResourceHandling(ResourceConfigurer configurer) {
			configurer.addPathMapping("/resources/**").addResourceLocation("/resources/");
		}

		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			configurer.enable();
		}

		@Bean
		public UrlBasedViewResolver urlBasedViewResolver() {
			UrlBasedViewResolver resolver = new UrlBasedViewResolver();
			resolver.setViewClass(TilesView.class);
			return resolver;
		}
		
		@Bean
		public TilesConfigurer tilesConfigurer() {
			TilesConfigurer configurer = new TilesConfigurer();
			configurer.setDefinitions(new String[] {"/WEB-INF/**/tiles.xml"});
			return configurer;
		}
		
		@Bean
		public TestController testController() {
			return new TestController();
		}
	}
	
	public enum ConfigType { XML, ANNOT }
	
}
