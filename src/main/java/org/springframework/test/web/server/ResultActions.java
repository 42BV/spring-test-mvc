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
 * A contract for defining actions on the results of an executed request.
 * 
 * <p>See static factory methods in
 * {@code org.springframework.test.web.server.result.MockMvcResultMatchers} and
 * {@code org.springframework.test.web.server.result.MockMvcResultHandlers}.
 * 
 * @author Rossen Stoyanchev
 */
public interface ResultActions {

	/**
	 * Provide an expectation. For example:
	 * <pre>
	 * // Assuming static import of MockMvcResultMatchers.*
	 * 
	 * mockMvc.perform(get("/person/1"))
	 *   .andExpect(status.isOk())
	 *   .andExpect(content().type(MediaType.APPLICATION_JSON))
	 *   .andExpect(jsonPath("$.person.name").equalTo("Jason"));
	 *   
	 * mockMvc.perform(post("/form"))
	 *   .andExpect(status.isOk())
	 *   .andExpect(redirectedUrl("/person/1"))
	 *   .andExpect(model().size(1))
	 *   .andExpect(model().attributeExists("person"))
	 *   .andExpect(flash().attributeCount(1))
	 *   .andExpect(flash().attribute("message", "success!"));
	 * </pre> 
	 */
	ResultActions andExpect(ResultMatcher matcher) throws Exception;

	/**
	 * Provide a general action. For example:
	 * <pre>
	 * // Assuming static imports of MockMvcResultHandlers.* and MockMvcResultMatchers.*
	 * 
	 * mockMvc.perform(get("/form"))
	 *   .andDo(print())         // Print the results
	 *   .andExpect(status.isOk())
	 *   .andExpect(contentType(MediaType.APPLICATION_JSON));
	 * </pre>
	 */
	ResultActions andDo(ResultHandler handler) throws Exception;
	
}