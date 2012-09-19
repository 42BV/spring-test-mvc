/*
 * Copyright 2011-2012 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.NestedRuntimeException;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.test.web.server.MockFilterChain;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.RequestBuilder;
import org.springframework.test.web.server.ResultHandler;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.TestDispatcherServlet;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * An abstract class for building {@link MockMvc} instances.
 *
 * <p>Provides support for configuring {@link Filter}s and mapping them to URL
 * patterns as defined by the Servlet specification.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 */
public abstract class AbstractMockMvcBuilder<Self extends AbstractMockMvcBuilder<Self>> implements MockMvcBuilder {

	private List<Filter> filters = new ArrayList<Filter>();

	private RequestBuilder requestBuilder;

	private final List<ResultMatcher> resultMatchers = new ArrayList<ResultMatcher>();

	private final List<ResultHandler> resultHandlers = new ArrayList<ResultHandler>();


	/**
	 * Build a {@link MockMvc} instance.
	 */
	public final MockMvc build() {

		ServletContext servletContext = initServletContext();
		WebApplicationContext wac = initWebApplicationContext(servletContext);

		ServletConfig config = new MockServletConfig(servletContext);
		TestDispatcherServlet dispatcherServlet = new TestDispatcherServlet(wac);
		try {
			dispatcherServlet.init(config);
		}
		catch (ServletException ex) {
			// should never happen..
			throw new MockMvcBuildException("Failed to initialize TestDispatcherServlet", ex);
		}

		Filter[] filterArray = filters.toArray(new Filter[filters.size()]);
		MockFilterChain mockMvcFilterChain = new MockFilterChain(dispatcherServlet, filterArray) {};

		return new MockMvc(mockMvcFilterChain, dispatcherServlet.getServletContext()) {{
			setDefaultRequest(AbstractMockMvcBuilder.this.requestBuilder);
			setDefaultResultMatchers(AbstractMockMvcBuilder.this.resultMatchers);
			setDefaultResultHandlers(AbstractMockMvcBuilder.this.resultHandlers);
		}};
	}

	/**
	 * Define a default request that all performed requests should logically
	 * extend from. In effect this provides a mechanism for defining common
	 * initialization for all requests such as the content type, request
	 * parameters, session attributes, and any other request property.
	 *
	 * <p>Properties specified at the time of performing a request override the
	 * default properties defined here.
	 *
	 * @param requestBuilder a RequestBuilder; see static factory methods in
	 * {@link org.springframework.test.web.server.request.MockMvcRequestBuilders}
	 * .
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Self> T defaultRequest(RequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		return (T) this;
	}

	/**
	 * Define an expectation that should <em>always</em> be applied to every
	 * response. For example, status code 200 (OK), content type
	 * {@code "application/json"}, etc.
	 *
	 * @param resultMatcher a ResultMatcher; see static factory methods in
	 * {@link org.springframework.test.web.server.result.MockMvcResultMatchers}
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Self> T alwaysExpect(ResultMatcher resultMatcher) {
		this.resultMatchers.add(resultMatcher);
		return (T) this;
	}

	/**
	 * Define an action that should <em>always</em> be applied to every
	 * response. For example, writing detailed information about the performed
	 * request and resulting response to {@code System.out}.
	 *
	 * @param resultHandler a ResultHandler; see static factory methods in
	 * {@link org.springframework.test.web.server.result.MockMvcResultHandlers}
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Self> T alwaysDo(ResultHandler resultHandler) {
		this.resultHandlers.add(resultHandler);
		return (T) this;
	}

	/**
	 * Add filters mapped to any request (i.e. "/*"). For example:
	 *
	 * <pre class="code">
	 * mockMvcBuilder.addFilters(springSecurityFilterChain);
	 * </pre>
	 *
	 * <p>is the equivalent of the following web.xml configuration:
	 *
	 * <pre class="code">
	 * &lt;filter-mapping&gt;
	 *     &lt;filter-name&gt;springSecurityFilterChain&lt;/filter-name&gt;
	 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
	 * &lt;/filter-mapping&gt;
	 * </pre>
	 *
	 * <p>Filters will be invoked in the order in which they are provided.
	 *
	 * @param filters the filters to add
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Self> T addFilters(Filter... filters) {
		Assert.notNull(filters, "filters cannot be null");

		for(Filter f : filters) {
			Assert.notNull(f, "filters cannot contain null values");
			this.filters.add(f);
		}
		return (T) this;
	}

	/**
	 * Add a filter mapped to a specific set of patterns. For example:
	 *
	 * <pre class="code">
	 * mockMvcBuilder.addFilters(myResourceFilter, "/resources/*");
	 * </pre>
	 *
	 * <p>is the equivalent of:
	 *
	 * <pre class="code">
	 * &lt;filter-mapping&gt;
	 *     &lt;filter-name&gt;myResourceFilter&lt;/filter-name&gt;
	 *     &lt;url-pattern&gt;/resources/*&lt;/url-pattern&gt;
	 * &lt;/filter-mapping&gt;
	 * </pre>
	 *
	 * <p>Filters will be invoked in the order in which they are provided.
	 *
	 * @param filter the filter to add
	 * @param urlPatterns URL patterns to map to; if empty, "/*" is used by default
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Self> T addFilter(Filter filter, String... urlPatterns) {
		Assert.notNull(filter, "filter cannot be null");
		Assert.notNull(urlPatterns, "urlPatterns cannot be null");

		if(urlPatterns.length > 0) {
			filter = new PatternMappingFilterProxy(filter, urlPatterns);
		}

		this.filters.add(filter);
		return (T) this;
	}

	/**
	 * Return ServletContext to use, never {@code null}.
	 */
	protected abstract ServletContext initServletContext();

	/**
	 * Return the WebApplicationContext to use, possibly {@code null}.
	 * @param servletContext the ServletContext returned
	 * from {@link #initServletContext()}
	 */
	protected abstract WebApplicationContext initWebApplicationContext(ServletContext servletContext);


	@SuppressWarnings("serial")
	private static class MockMvcBuildException extends NestedRuntimeException {

		public MockMvcBuildException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}
}
