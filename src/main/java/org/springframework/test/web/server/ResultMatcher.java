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

package org.springframework.test.web.server;


/**
 * A contract to match the results of an executed request against some expectation.
 *
 * <p>See static factory methods in
 * {@code org.springframework.test.web.server.result.MockMvcResultActions}.
 *
 * <p>Example, assuming a static import of {@code MockMvcRequestBuilders.*} and
 * {@code MockMvcResultActions.*}:
 *
 * <pre>
 * mockMvc.perform(get("/form"))
 *   .andExpect(status.isOk())
 *   .andExpect(content().mimeType(MediaType.APPLICATION_JSON));
 * </pre>
 *
 * @author Rossen Stoyanchev
 */
public interface ResultMatcher {

	/**
	 * Match the result of an executed Spring MVC request to an expectation.
	 * @param mvcResult TODO
	 * @throws Exception if a failure occurs while printing
	 */
	void match(MvcResult mvcResult) throws Exception;

}
