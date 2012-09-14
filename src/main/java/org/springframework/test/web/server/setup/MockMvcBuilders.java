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

package org.springframework.test.web.server.setup;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * The main class to import to access all available {@link MockMvcBuilder}s.
 *
 * <p><strong>Eclipse users:</strong> consider adding this class as a Java editor
 * favorite. To navigate, open the Preferences and type "favorites".
 *
 * @author Rossen Stoyanchev
 */
public class MockMvcBuilders {

	/**
	 * Build a {@link MockMvc} from Java-based Spring configuration.
	 * @param configClasses one or more @{@link Configuration} classes
	 */
	public static ContextMockMvcBuilder annotationConfigSetup(Class<?>... configClasses) {
		Assert.notEmpty(configClasses, "At least one @Configuration class is required");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(configClasses);
		return new ContextMockMvcBuilder(context);
	}

	/**
	 * Build a {@link MockMvc} from XML-based Spring configuration.
	 * @param configLocations XML configuration file locations:
	 * 	<ul>
	 * 		<li>{@code classpath:org/example/config/*-context.xml}
	 * 		<li>{@code file:src/main/webapp/WEB-INF/config/*-context.xml}
	 * 		<li>etc.
	 * </ul>
	 */
	public static ContextMockMvcBuilder xmlConfigSetup(String... configLocations) {
		Assert.notEmpty(configLocations, "At least one XML config location is required");
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(configLocations);
		return new ContextMockMvcBuilder(context);
	}

	/**
	 * Build a {@link MockMvc} from a fully initialized {@link WebApplicationContext}
	 * The context must have been setup with a {@link ServletContext} and refreshed.
	 */
	public static InitializedContextMockMvcBuilder webApplicationContextSetup(WebApplicationContext context) {
		return new InitializedContextMockMvcBuilder(context);
	}

	/**
	 * Build a {@link MockMvc} by providing @{@link Controller} instances and configuring
	 * directly the required Spring MVC components rather than having them looked up in
	 * a Spring ApplicationContext.
	 * @param controllers one or more controllers with @{@link RequestMapping} methods
	 */
	public static StandaloneMockMvcBuilder standaloneSetup(Object... controllers) {
		return new StandaloneMockMvcBuilder(controllers);
	}

}
