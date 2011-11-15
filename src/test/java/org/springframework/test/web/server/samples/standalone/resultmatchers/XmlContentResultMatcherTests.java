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

package org.springframework.test.web.server.samples.standalone.resultmatchers;

import static org.hamcrest.Matchers.hasXPath;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.samples.standalone.Person;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Examples of expectations on XML response content. 
 * 
 * @author Rossen Stoyanchev
 * 
 * @see XpathResultMatcherTests
 */
public class XmlContentResultMatcherTests {

	private static final String PEOPLE_XML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + 
		"<ns2:people xmlns:ns2=\"http://example.org/music/people\"><composers>" +
		"<composer><name>Johann Sebastian Bach</name><someBoolean>false</someBoolean><someDouble>21.0</someDouble></composer>" +
		"<composer><name>Johannes Brahms</name><someBoolean>false</someBoolean><someDouble>0.0025</someDouble></composer>" + 
		"<composer><name>Edvard Grieg</name><someBoolean>false</someBoolean><someDouble>1.6035</someDouble></composer>" + 
		"<composer><name>Robert Schumann</name><someBoolean>false</someBoolean><someDouble>NaN</someDouble></composer>" + 
		"</composers></ns2:people>";

	private static final Map<String, String> NAMESPACES = 
			Collections.singletonMap("ns", "http://example.org/music/people");

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(new MusicController()).build();
	}
	
	@Test
	public void testXmlEqualTo() throws Exception {
		this.mockMvc.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
			.andExpect(content().xml(PEOPLE_XML));
	}

	@Test
	public void testNodeMatcher() throws Exception {
		
		SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
		nsContext.setBindings(NAMESPACES);
		
		this.mockMvc.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
			.andExpect(content().node(hasXPath("/ns:people/composers/composer[1]", nsContext)));
	}
	
	
	@Controller
	@SuppressWarnings("unused")
	private static class MusicController {

		@RequestMapping(value="/music/people")
		public @ResponseBody PeopleWrapper getPeople() {
			
			List<Person> composers = Arrays.asList(
					new Person("Johann Sebastian Bach").setSomeDouble(21), 
					new Person("Johannes Brahms").setSomeDouble(.0025), 
					new Person("Edvard Grieg").setSomeDouble(1.6035), 
					new Person("Robert Schumann").setSomeDouble(Double.NaN));
			
			return new PeopleWrapper(composers);
		}
	}
	
	@SuppressWarnings("unused")
	@XmlRootElement(name="people", namespace="http://example.org/music/people")
	@XmlAccessorType(XmlAccessType.FIELD)
	private static class PeopleWrapper {

		@XmlElementWrapper(name="composers")
		@XmlElement(name="composer")
		private List<Person> composers;
	
		public PeopleWrapper() {
		}

		public PeopleWrapper(List<Person> composers) {
			this.composers = composers;
		}

		public List<Person> getComposers() {
			return this.composers;
		}
	}
}
