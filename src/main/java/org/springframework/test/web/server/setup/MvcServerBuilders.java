package org.springframework.test.web.server.setup;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.server.MockMvcServer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class MvcServerBuilders {

	/**
	 * Build a {@link MockMvcServer} from a set of controllers with @{@link RequestMapping} methods.
	 * 
	 * @param controllers controllers with @{@link RequestMapping} methods to include in the setup
	 */
	public static StandaloneServerBuilder standaloneSetup(Object...controllers) {
		return new StandaloneServerBuilder(controllers);
	}

	/**
	 * Create  a {@link ConfigurableContextServerBuilder} from Spring Java-based configuration. 
	 * 
	 * @param configurationClasses @{@link Configuration} classes to use to create a WebApplicationContext
	 */
	public static ConfigurableContextServerBuilder annotationConfigSetup(Class<?>...configurationClasses) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(configurationClasses);
		return new ConfigurableContextServerBuilder(context);
	}
	
	/**
	 * Create a {@link ConfigurableContextServerBuilder} from Spring XML configuration. 
	 * 
	 * @param configLocations XML configuration file locations<br>For example:
	 * <ul>
	 * 	<li>{@code classpath:org/example/config/*-context.xml}
	 * 	<li>{@code file:src/main/webapp/WEB-INF/config/*-context.xml}
	 * </ul>
	 */
	public static ConfigurableContextServerBuilder xmlConfigSetup(String...configLocations) {
		Assert.notEmpty(configLocations, "At least one XML config location is required");
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setConfigLocations(configLocations);
		return new ConfigurableContextServerBuilder(context);
	}

	/**
	 * Bulid a {@link MockMvcServer} from a fully initialized {@link WebApplicationContext}. 
	 * This may be useful if you already have a context initialized through the Spring TestContext framework.
	 */
	public static AbstractContextServerBuilder contextSetup(WebApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "WebApplicationContext is required");
		Assert.notNull(applicationContext.getServletContext(), "WebApplicationContext must have a ServletContext");
		return new SimpleContextServerBuilder(applicationContext);
	}

	/**
	 * A {@link AbstractContextServerBuilder} that uses a previously fully initialized {@link WebApplicationContext}.
	 */
	private static class SimpleContextServerBuilder extends AbstractContextServerBuilder {

		private final WebApplicationContext applicationContext;

		public SimpleContextServerBuilder(WebApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@Override
		protected WebApplicationContext getApplicationContext() {
			return applicationContext;
		}

		@Override
		protected WebApplicationContext initApplicationContext() {
			return applicationContext;
		}
	}

}
