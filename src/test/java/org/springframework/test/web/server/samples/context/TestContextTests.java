package org.springframework.test.web.server.samples.context;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
import static org.springframework.test.web.server.setup.MockMvcBuilders.applicationContextSetup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Tests that use bean definitions from an ApplicationContext created through 
 * the Spring TestContext framework.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestContextTests {

	@Autowired
	ConfigurableApplicationContext context;

	@Test
	public void responseBodyHandler() throws Exception {

		MockMvc mockMvc = 
			applicationContextSetup(context)
				.configureWebAppRootDir("src/test/webapp", false)
				.build();

		mockMvc.perform(get("/form")).andExpect(response().status().isOk());
		
		mockMvc.perform(get("/wrong")).andExpect(response().status().isNotFound());
	}

	@Controller
	static class TestController {

		@RequestMapping("/form")
		@ResponseBody
		public String form() {
			return "hello";
		}

	}
}
