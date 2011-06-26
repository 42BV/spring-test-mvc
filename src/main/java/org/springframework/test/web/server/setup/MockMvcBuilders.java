package org.springframework.test.web.server.setup;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class MockMvcBuilders {

	/**
	 * Build a {@link MockMvc} from a set of controllers with @{@link RequestMapping} methods.
	 * 
	 * @param controllers controllers with @{@link RequestMapping} methods to include in the setup
	 */
	public static StandaloneMockMvcBuilder standaloneMvcSetup(Object...controllers) {
		return new StandaloneMockMvcBuilder(controllers);
	}

	/**
	 * Create  a {@link ContextMockMvcBuilder} from Spring Java-based configuration. 
	 * 
	 * @param configClasses @{@link Configuration} classes to use to create a WebApplicationContext
	 */
	public static ContextMockMvcBuilder annotationConfigMvcSetup(Class<?>...configClasses) {
		Assert.notEmpty(configClasses, "At least one @Configuration class is required");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(configClasses);
		return new ContextMockMvcBuilder(context);
	}
	
	/**
	 * Create a {@link ContextMockMvcBuilder} from Spring XML configuration. 
	 * 
	 * @param configLocations XML configuration file locations<br>For example:
	 * <ul>
	 * 	<li>{@code classpath:org/example/config/*-context.xml}
	 * 	<li>{@code file:src/main/webapp/WEB-INF/config/*-context.xml}
	 * </ul>
	 */
	public static ContextMockMvcBuilder xmlConfigMvcSetup(String...configLocations) {
		Assert.notEmpty(configLocations, "At least one XML config location is required");
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(configLocations);
		return new ContextMockMvcBuilder(context);
	}

	/**
	 * Build a {@link MockMvc} from a fully initialized {@link WebApplicationContext}. 
	 * This may be useful if you already have a context initialized through the Spring TestContext framework.
	 */
	public static AbstractContextMockMvcBuilder applicationContextMvcSetup(WebApplicationContext context) {
		return new InitializedContextMockMvcBuilder(context);
	}

}
