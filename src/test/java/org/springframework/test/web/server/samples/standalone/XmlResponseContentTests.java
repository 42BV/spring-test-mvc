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

package org.springframework.test.web.server.samples.standalone;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultActions.response;
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

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Tests with XML response content.
 *
 * @author Rossen Stoyanchev
 */
public class XmlResponseContentTests {

	private static final String PEOPLE_XML = "<ns:people xmlns:ns=\"http://example.org/music/people\">"
			+ "<composers><composer><name>Johann Sebastian Bach</name></composer><composer><name>Johannes Brahms</name></composer>"
			+ "<composer><name>Edvard Grieg</name></composer><composer><name>Robert Schumann</name></composer></composers>"
			+ "<performers><performer><name>Vladimir Ashkenazy</name></performer><performer><name>Yehudi Menuhin</name></performer></performers>"
			+ "</ns:people>";

	private static final Map<String, String> NAMESPACES = 
		Collections.singletonMap("ns", "http://example.org/music/people");

	@Test
	public void isEqualToXml() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().isEqualToXml(PEOPLE_XML));
	}
	
	@Test
	public void xpathExists() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().xpath("/ns:people/composers", NAMESPACES).exists())
				.andExpect(response().content().xpath("/ns:people/composers[1]/composer", NAMESPACES).exists())
				.andExpect(response().content().xpath("/ns:people/composers[1]/composer/name/text()", NAMESPACES).exists());
	}
	
	@Test
	public void xpathDoesNotExist() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().xpath("/ns:people/performers[3]", NAMESPACES).doesNotExist());
	}
	
	@Test
	public void xpathEvaluatesTo() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().xpath("/ns:people/composers[1]/composer/name/text()", NAMESPACES)
						.evaluatesTo("Johann Sebastian Bach"));
	}
	
	@Test
	public void xpathAsText() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().xpath("/ns:people/composers[1]/composer/name/text()", NAMESPACES)
						.asText(containsString("Sebastian")));
	}

	@Test
	public void xpathNodeCount() throws Exception {
		standaloneSetup(new MusicController()).build()
			.perform(get("/music/people").accept(MediaType.APPLICATION_XML))
				.andExpect(response().status(HttpStatus.OK))
				.andExpect(response().contentType(MediaType.APPLICATION_XML))
				.andExpect(response().content().xpath("/ns:people/composers/composer", NAMESPACES).nodeCount(4));
	}

	
	@Controller
	@SuppressWarnings("unused")
	private static class MusicController {

		@RequestMapping(value="/music/people")
		public @ResponseBody PeopleWrapper getPeople() {
			
			List<Person> composers = Arrays.asList(
					new Person("Johann Sebastian Bach"), new Person("Johannes Brahms"), 
					new Person("Edvard Grieg"), new Person("Robert Schumann"));
			
			List<Person> performers = Arrays.asList(
					new Person("Vladimir Ashkenazy"), new Person("Yehudi Menuhin"));
			
			return new PeopleWrapper(composers, performers);
		}
	}
	
	@SuppressWarnings("unused")
	@XmlRootElement(name="people", namespace="http://example.org/music/people")
	@XmlAccessorType(XmlAccessType.FIELD)
	private static class PeopleWrapper {

		@XmlElementWrapper(name="composers")
		@XmlElement(name="composer")
		private List<Person> composers;
	
		@XmlElementWrapper(name="performers")
		@XmlElement(name="performer")
		private List<Person> performers;
		
		public PeopleWrapper() {
		}

		public PeopleWrapper(List<Person> composers, List<Person> performers) {
			this.composers = composers;
			this.performers = performers;
		}

		public List<Person> getComposers() {
			return this.composers;
		}

		public List<Person> getPerformers() {
			return this.performers;
		}
	}

}
