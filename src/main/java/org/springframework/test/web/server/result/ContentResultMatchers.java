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

package org.springframework.test.web.server.result;

import static org.springframework.test.web.AssertionErrors.assertEquals;
import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.XmlExpectationsHelper;
import org.w3c.dom.Node;

/**
 * Factory for response content {@code ResultMatcher}'s. An instance of this
 * class is typically accessed via {@link MockMvcResultMatchers#content()}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ContentResultMatchers {

	private final XmlExpectationsHelper xmlHelper;

	public ContentResultMatchers() {
		this.xmlHelper = new XmlExpectationsHelper();
	}

	/**
	 * Assert the ServletResponse content type.
	 */
	public ResultMatcher type(String contentType) {
		return type(MediaType.parseMediaType(contentType));
	}

	/**
	 * Assert the ServletResponse content type after parsing it as a MediaType.
	 */
	public ResultMatcher type(final MediaType contentType) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String actual = result.getResponse().getContentType();
				assertTrue("Content type not set", actual != null);
				assertEquals("Content type", contentType, MediaType.parseMediaType(actual));
			}
		};
	}

	/**
	 * Assert the character encoding in the ServletResponse.
	 * @see HttpServletResponse#getCharacterEncoding()
	 */
	public ResultMatcher encoding(final String characterEncoding) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				String actual = result.getResponse().getCharacterEncoding();
				assertEquals("Character encoding", characterEncoding, actual);
			}
		};
	}

	/**
	 * Apply a {@link Matcher} to the response content. For example:
	 * <pre>
	 * mockMvc.perform(get("/path"))
	 *   .andExpect(content(containsString("text")));
	 * </pre>
	 */
	public ResultMatcher string(final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				MatcherAssert.assertThat("Response content", result.getResponse().getContentAsString(), matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher string(String content) {
		return string(Matchers.equalTo(content));
	}

	/**
	 * TODO
	 */
	public ResultMatcher bytes(final byte[] expectedContent) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				byte[] content = result.getResponse().getContentAsByteArray();
				MatcherAssert.assertThat("Response content", content, Matchers.equalTo(expectedContent));
			}
		};
	}

	/**
	 * Parse the response content and the given string as XML and assert the
	 * two are "similar" - i.e. they contain the same elements and attributes
	 * regardless of order.
	 * <p>Use of this matcher requires the
	 * <a href="http://xmlunit.sourceforge.net/">XMLUnit<a/> library.
	 * @param xmlContent the expected XML content
	 * @see MockMvcResultMatchers#xpath(String, Object...)
	 * @see MockMvcResultMatchers#xpath(String, Map, Object...)
	 */
	public ResultMatcher xml(final String xmlContent) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xmlHelper.assertXmlEqual(xmlContent, content);
			}
		};
	}

	// TODO: XML validation

	/**
	 * Parse the response content as {@link Node} and apply the given {@link Matcher}.
	 * @see org.hamcrest.Matchers#hasXPath
	 */
	public ResultMatcher node(final Matcher<? super Node> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xmlHelper.assertNode(content, matcher);
			}
		};
	}

	/**
	 * Parse the response content as {@link DOMSource} and apply the given {@link Matcher}.
	 * @see <a href="http://code.google.com/p/xml-matchers/">xml-matchers</a>
	 */
	public ResultMatcher source(final Matcher<? super Source> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xmlHelper.assertSource(content, matcher);
			}
		};
	}

}
