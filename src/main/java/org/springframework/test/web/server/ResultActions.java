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
 * A contract for defining actions such as expectations on the result of an 
 * executed Spring MVC request using chained methods.
 * 
 * <p>Note that instead of creating {@link ResultMatcher} instances directly, 
 * tests will rather create expectations (and other actions) via chained 
 * static methods the main entry point for which is in 
 * {@code org.springframework.test.web.server.result.MockMvcResultActions}.
 * 
 * <p>Below is a short example:
 * <pre>
 *   // Assumes static import of MockMvcResultActions.*
 * 
 *   mockMvc.perform(get("/form"))
 *     .andExpect(response().status(HttpStatus.OK))
 *     .andPrintTo(console());
 * </pre> 
 * 
 * @author Rossen Stoyanchev
 */
public interface ResultActions {

	/**
	 * Define an expectation.
	 * {@code org.springframework.test.web.server.result.MockMvcResultActions}
	 */
	ResultActions andExpect(ResultMatcher matcher) throws Exception;

	/**
	 * Define a print action.
	 * @see org.springframework.test.web.server.result.MockMvcResultActions#toConsole()
	 */
	ResultActions andPrint(ResultPrinter printer) throws Exception;
	
}