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

import javax.servlet.RequestDispatcher;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link AbstractContextServerBuilder} variant that expects a Spring {@link ConfigurableWebApplicationContext} and 
 * provides methods to further initialize the context by setting active profiles, applying 
 * {@link ApplicationContextInitializer}s to it and so on.
 * 
 */
public class ConfigurableContextServerBuilder extends AbstractContextServerBuilder {

	private final ConfigurableWebApplicationContext applicationContext;
	
	private String webResourceBasePath = "";

	private ResourceLoader webResourceLoader = new FileSystemResourceLoader();
	
	protected ConfigurableContextServerBuilder(ConfigurableWebApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "ApplicationContext is required");
		this.applicationContext = applicationContext;
	}

	@Override
	protected WebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Specify the location of web application root directory. 
	 * 
	 * <p>If {@code isClasspathRelative} is {@code false} the directory path may be relative to the JVM working 
	 * directory (e.g. "src/main/webapp") or fully qualified (e.g. "file:///home/user/webapp"). Or otherwise it 
	 * should be relative to the classpath (e.g. "org/examples/myapp/config"). 
	 *  
	 * @param warRootDir the Web application root directory (should not end with a slash)
	 */
	public ConfigurableContextServerBuilder configureWarRootDir(String warRootDir, boolean isClasspathRelative) {
		this.webResourceBasePath = warRootDir;
		this.webResourceLoader = isClasspathRelative ? new DefaultResourceLoader() : new FileSystemResourceLoader();
		return this;
	}
	
	public ConfigurableContextServerBuilder activateProfiles(String...profiles) {
		applicationContext.getEnvironment().setActiveProfiles(profiles);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ConfigurableWebApplicationContext> 
			ConfigurableContextServerBuilder applyInitializers(ApplicationContextInitializer<T>... initializers) {
		
		for (ApplicationContextInitializer<T> initializer : initializers) {
			initializer.initialize((T) applicationContext);
		}
		return this;
	}
	
	@Override
	protected WebApplicationContext initApplicationContext() {
		
		MockServletContext servletContext = new MockServletContext(webResourceBasePath, webResourceLoader) {
			// For DefaultServletHttpRequestHandler ..
			public RequestDispatcher getNamedDispatcher(String path) {
				return (path.equals("default")) ? 
						new MockRequestDispatcher(path) : super.getNamedDispatcher(path); 
			}			
		};
		
		applicationContext.setServletContext(servletContext);
		applicationContext.refresh();
		
		return applicationContext;
	}

}
