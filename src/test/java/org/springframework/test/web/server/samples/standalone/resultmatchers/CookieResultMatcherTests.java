/*
 * Copyright 2011 the original author or authors.
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

package org.springframework.test.web.server.samples.standalone.resultmatchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Examples of expectations on response cookies values.
 *
 * @author Rossen Stoyanchev
 */
public class CookieResultMatcherTests {

	private MockMvc mockMvc;

	@Before
	public void setup() {

		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setCookieDomain("domain");

		this.mockMvc = standaloneSetup(new SimpleController())
				.addInterceptors(new LocaleChangeInterceptor())
				.setLocaleResolver(localeResolver)
				.build();
	}

	@Test
	public void testExists() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().exists(CookieLocaleResolver.DEFAULT_COOKIE_NAME));
	}

	@Test
	public void testNotExists() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().doesNotExist("unknowCookie"));
	}

	@Test
	public void testEqualTo() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
			.andExpect(cookie().value(CookieLocaleResolver.DEFAULT_COOKIE_NAME, "en_US"));

		// Hamcrest matchers...
		this.mockMvc.perform(get("/").param("locale", "en_US"))
			.andExpect(cookie().value(CookieLocaleResolver.DEFAULT_COOKIE_NAME, equalTo("en_US")));
	}

	@Test
	public void testMatcher() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
			.andExpect(cookie().value(CookieLocaleResolver.DEFAULT_COOKIE_NAME, startsWith("en")));
	}

	@Test
	public void testMaxAge() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().maxAge(CookieLocaleResolver.DEFAULT_COOKIE_NAME, -1));
	}

	@Test
	public void testDomain() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().domain(CookieLocaleResolver.DEFAULT_COOKIE_NAME, "domain"));
	}

	@Test
	public void testVersion() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().version(CookieLocaleResolver.DEFAULT_COOKIE_NAME, 0));
	}

	@Test
	public void testPath() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().path(CookieLocaleResolver.DEFAULT_COOKIE_NAME, "/"));
	}

	@Test
	public void testSecured() throws Exception {
		this.mockMvc.perform(get("/").param("locale", "en_US"))
				.andExpect(cookie().secure(CookieLocaleResolver.DEFAULT_COOKIE_NAME, false));
	}

	@Controller
	@SuppressWarnings("unused")
	private static class SimpleController {

		@RequestMapping("/")
		public String home() {
			return "home";
		}
	}
}
