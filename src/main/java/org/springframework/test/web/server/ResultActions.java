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

package org.springframework.test.web.server;

/**
 * Expose the result of an executed Spring MVC request to allow setting up match 
 * expectations with {@link ResultMatcher}s or to print with {@link ResultPrinter}s.
 * 
 * <p>Access all available result matchers and printers through:
 * {@code org.springframework.test.web.server.result.MockMvcResultActions}.
 * 
 * @author Rossen Stoyanchev
 */
public interface ResultActions {

	/**
	 * Invoke a {@link ResultMatcher} to assert the result of an executed Spring MVC request.
	 * @param matcher the matcher to invoke
	 */
	ResultActions andExpect(ResultMatcher matcher);

	/**
	 * Invoke a {@link ResultPrinter} to print the result of an executed Spring MVC request.
	 * @param printer the printer to invoke
	 */
	void andPrintTo(ResultPrinter printer);

}