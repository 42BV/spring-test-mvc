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
 * Provides an extension point for applications or 3rd party libraries that wish
 * to provide additional request-building methods without actually having to
 * extend {@link DefaultRequestBuilder}.
 *
 * <p>Implementation can be plugged in via
 * {@link DefaultRequestBuilder#with(RequestBuilderInitializer)}. For example:
 *
 * <pre>
 * mockMvc.perform(get("/accounts").accept("application/json").with(login("user", "password")));
 * </pre>
 *
 * @author Rossen Stoyanchev
 */
public interface RequestBuilderInitializer {

	/**
	 * Initialize the given {@code DefaultRequestBuilder}.
	 *
	 * @param requestBuilder the requestBuilder to initialize
	 */
	void initialize(DefaultRequestBuilder requestBuilder);

}
