/*
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.test.web.server.request;

/**
 * Extension point for applications or libraries that wish to provide additional
 * methods for building a request without actually extending from
 * {@link MockHttpServletRequestBuilder} as well as its sub-class
 * {@link MockMultipartHttpServletRequestBuilder}.
 *
 * <p>Implementations of this interface can be plugged in via
 * {@link MockHttpServletRequestBuilder#with(RequestBuilderInitializer)}.
 *
 * <p>Example showing how a custom {@code login} builder method would be used
 * to perform a request:
 *
 * <pre>
 * mockMvc.perform(get(&quot;/accounts&quot;).accept(&quot;application/json&quot;).with(login(&quot;user&quot;, &quot;password&quot;)));
 * </pre>
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 */
public interface RequestBuilderInitializer {

	/**
	 * Initialize the given {@code DefaultRequestBuilder}.
	 *
	 * @param requestBuilder the requestBuilder to initialize
	 */
	void initialize(MockHttpServletRequestBuilder requestBuilder);

}
