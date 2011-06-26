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
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Expects a {@link ConfigurableWebApplicationContext} that has not been initialized yet. 
 * Provides builder style methods to further configure the {@link WebApplicationContext}
 * including initialization of its {@link ServletContext}.
 */
public class ContextMockMvcBuilder extends AbstractContextMockMvcBuilder {

	private final ConfigurableWebApplicationContext applicationContext;
	
	private String webResourceBasePath = "";

	private ResourceLoader webResourceLoader = new FileSystemResourceLoader();
	
	protected ContextMockMvcBuilder(ConfigurableWebApplicationContext applicationContext) {
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
	public ContextMockMvcBuilder configureWarRootDir(String warRootDir, boolean isClasspathRelative) {
		this.webResourceBasePath = warRootDir;
		this.webResourceLoader = isClasspathRelative ? new DefaultResourceLoader() : new FileSystemResourceLoader();
		return this;
	}
	
	/**
	 * TODO
	 * 
	 */
	public ContextMockMvcBuilder activateProfiles(String...profiles) {
		applicationContext.getEnvironment().setActiveProfiles(profiles);
		return this;
	}
	
	/**
	 * TODO
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends ConfigurableWebApplicationContext> 
			ContextMockMvcBuilder applyInitializers(ApplicationContextInitializer<T>... initializers) {
		
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
