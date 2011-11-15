/*
 * Copyright 2005-2011 the original author or authors.
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

/**
 * Contains {@link org.springframework.test.web.server.ResultMatcher}
 * implementations for setting up expectations on the results of an executed
 * request. Most of the implementations are anonymous classes available
 * through static methods via 
 * {@link org.springframework.test.web.server.result.MockMvcResultMatchers}.
 * 
 * <p>Also contains 
 * {@link org.springframework.test.web.server.result.MockMvcResultHandlers}
 * implementations with for general actions on the results of of an executed
 * request. Implementations are available thorugh static methods in
 * {@link org.springframework.test.web.server.result.MockMvcResultHandlers}. 
 */
package org.springframework.test.web.server.result;
