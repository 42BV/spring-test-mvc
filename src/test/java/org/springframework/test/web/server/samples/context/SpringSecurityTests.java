/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.test.web.server.samples.context;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader=WebContextLoader.class,
		classes=SpringSecurityTests.ContextConfiguration.class)
public class SpringSecurityTests {
	@Configuration
	@ImportResource("classpath:org/springframework/test/web/server/samples/context/security.xml")
	@Import(WebConfig.class)
	public static class ContextConfiguration {}

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webApplicationContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(redirectedUrl("http://localhost/spring_security_login"));
	}

	@Test
	public void accessGranted() throws Exception {
		TestingAuthenticationToken principal = new TestingAuthenticationToken("test", "", "ROLE_USER");
		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(principal);

		this.mockMvc.perform(get("/").sessionAttr(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,	securityContext))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

	@Test
	public void accessDenied() throws Exception {
		TestingAuthenticationToken principal = new TestingAuthenticationToken("test", "", "ROLE_DENIED");
		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(principal);

		this.mockMvc.perform(get("/").sessionAttr(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext))
			.andExpect(status().isForbidden());
	}

	@Test
	public void userAuthenticates() throws Exception {
		final String username = "user";
		mockMvc.perform(post("/j_spring_security_check").param("j_username", username).param("j_password", "password"))
			.andExpect(redirectedUrl("/"))
			.andExpect(new ResultMatcher() {
				public void match(MvcResult mvcResult) throws Exception {
					HttpSession session = mvcResult.getRequest().getSession();
					SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
					Assert.assertEquals(securityContext.getAuthentication().getName(), username);
				}
			});
	}

	@Test
	public void userAuthenticateFails() throws Exception {
		final String username = "user";
		mockMvc.perform(post("/j_spring_security_check").param("j_username", username).param("j_password", "invalid"))
			.andExpect(redirectedUrl("/spring_security_login?login_error"))
			.andExpect(new ResultMatcher() {
				public void match(MvcResult mvcResult) throws Exception {
					HttpSession session = mvcResult.getRequest().getSession();
					SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
					Assert.assertNull(securityContext);
				}
			});
	}
}
