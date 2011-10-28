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

package org.springframework.test.web.server.samples.context;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * TestContext framework tests.
 * 
 * The TestContext framework doesn't support WebApplicationContext yet: 
 * https://jira.springsource.org/browse/SPR-5243
 * 
 * A custom {@link ContextLoader} loads a WebApplicationContext.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader=TestGenericWebXmlContextLoader.class,
		locations={"/org/springframework/test/web/server/samples/servlet-context.xml"})
public class TestContextTests {

	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webApplicationContextSetup(this.wac).build();
	}
	
	@Test
	public void tilesDefinitions() throws Exception {
		this.mockMvc.perform(get("/"))
				.andExpect(response().status().isOk())
				.andExpect(response().forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

}

class TestGenericWebXmlContextLoader extends GenericWebXmlContextLoader {

	public TestGenericWebXmlContextLoader() {
		super("src/test/resources/META-INF/web-resources", false);
	}
	
}

