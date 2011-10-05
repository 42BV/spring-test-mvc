package org.springframework.test.web.server.setup;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * A central class for access to all built-in {@link MockMvc} builders. 
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
	 * Build a {@link MockMvc} by copying bean definitions from a {@link ConfigurableApplicationContext}
	 * that may have been loaded for example through the Spring TestContext framework. The resulting
	 * context may further be initialized through the returned {@link ContextMockMvcBuilder}.
	 */
    public static ContextMockMvcBuilder applicationContextSetup(ConfigurableApplicationContext context) {
        GenericWebApplicationContext wac = new GenericWebApplicationContext();
        for(String name : context.getBeanFactory().getBeanDefinitionNames()) {
            wac.registerBeanDefinition(name, context.getBeanFactory().getBeanDefinition(name));
        }
        return new ContextMockMvcBuilder(wac);
    }

	/**
	 * Build a {@link MockMvc} from a fully initialized {@link WebApplicationContext},
	 * which may have been loaded for example through the Spring TestContext framework.
	 * The provided context must have been setup with a {@link ServletContext}.
	 */
	public static ContextMockMvcBuilderSupport webApplicationContextSetup(WebApplicationContext context) {
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
