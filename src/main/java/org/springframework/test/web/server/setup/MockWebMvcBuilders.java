package org.springframework.test.web.server.setup;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.server.MockWebMvc;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class MockWebMvcBuilders {

	/**
	 * Build a {@link MockWebMvc} from a set of controllers with @{@link RequestMapping} methods.
	 * 
	 * @param controllers controllers with @{@link RequestMapping} methods to include in the setup
	 */
	public static StandaloneSetupMvcBuilder standaloneMvcSetup(Object...controllers) {
		return new StandaloneSetupMvcBuilder(controllers);
	}

	/**
	 * Create  a {@link ConfigurableContextSetupMvcBuilder} from Spring Java-based configuration. 
	 * 
	 * @param configurationClasses @{@link Configuration} classes to use to create a WebApplicationContext
	 */
	public static ConfigurableContextSetupMvcBuilder annotationConfigMvcSetup(Class<?>...configurationClasses) {
		Assert.notEmpty(configurationClasses, "At least one @Configuration class is required");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(configurationClasses);
		return new ConfigurableContextSetupMvcBuilder(context);
	}
	
	/**
	 * Create a {@link ConfigurableContextSetupMvcBuilder} from Spring XML configuration. 
	 * 
	 * @param configLocations XML configuration file locations<br>For example:
	 * <ul>
	 * 	<li>{@code classpath:org/example/config/*-context.xml}
	 * 	<li>{@code file:src/main/webapp/WEB-INF/config/*-context.xml}
	 * </ul>
	 */
	public static ConfigurableContextSetupMvcBuilder xmlConfigMvcSetup(String...configLocations) {
		Assert.notEmpty(configLocations, "At least one XML config location is required");
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(configLocations);
		return new ConfigurableContextSetupMvcBuilder(context);
	}

	/**
	 * Bulid a {@link MockWebMvc} from a fully initialized {@link WebApplicationContext}. 
	 * This may be useful if you already have a context initialized through the Spring TestContext framework.
	 */
	public static ContextSetupMvcBuilder applicationContextMvcSetup(WebApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "WebApplicationContext is required");
		Assert.notNull(applicationContext.getServletContext(), "WebApplicationContext must have a ServletContext");
		return new ContextSetupMvcBuilder(applicationContext);
	}

}
