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

package org.springframework.test.web.server.result;

import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.result.ServletResponseResultMatchers.ServletResponseResultMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Provides methods to define expectations on the ServletResponse content.
 *
 * @author Rossen Stoyanchev
 */
public class ContentResultMatchers {
	
	/**
	 * Protected constructor. 
	 * @see MockMvcResultActions#response()
	 * @see ServletResponseResultMatchers#content()
	 */
	protected ContentResultMatchers() {
	}

	/**
	 * Match the response body to {@code expectedContent}.
	 */
	public ResultMatcher isEqualTo(final String expectedContent) {
		return asText(Matchers.equalTo(expectedContent));
	}	
	
	/**
	 * Match the response body with the given {@code Matcher<String>}.
	 * <p>Example:
	 * <pre>
	 * // import static org.hamcrest.Matchers.containsString;
	 * 
	 * mockMvc.perform(get("/path"))
	 *   .andExpect(response().content().asText(containsString("text")));
	 * </pre>
	 */
	public ResultMatcher asText(final Matcher<String> matcher) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				MatcherAssert.assertThat("Response content", response.getContentAsString(), matcher);
			}
		};
	}
	
	/**
	 * Match the response body with the given {@code Matcher<Node>}.
	 * @see org.hamcrest.Matchers#hasXPath
	 */
	public ResultMatcher asNode(final Matcher<Node> matcher) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				Document document = ResultMatcherUtils.toDocument(response.getContentAsString());
				MatcherAssert.assertThat("Response content", document, matcher);
			}
		};
	}

	/**
	 * Match the response body with the given {@code Matcher<Source>}.
	 * @see <a href="http://code.google.com/p/xml-matchers/">xml-matchers</a> 
	 */
	public ResultMatcher asSource(final Matcher<Source> matcher) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				Document document = ResultMatcherUtils.toDocument(response.getContentAsString());
				MatcherAssert.assertThat("Response content", new DOMSource(document), matcher);
			}
		};
	}

	/**
	 * Return a class with XPath result matchers.
	 * @param xpath the XPath expression to use in result matchers
	 */
	public XpathResultMatchers xpath(String xpath) {
		return new XpathResultMatchers(xpath, null);
	}

	/**
	 * Return a class with XPath result matchers.
	 * @param xpath the XPath expression to use in result matchers
	 * @param namespaces namespaces used in the XPath expression, or {@code null}
	 */
	public XpathResultMatchers xpath(String xpath, Map<String, String> namespaces) {
		return new XpathResultMatchers(xpath, namespaces);
	}

	/**
	 * Compare the response body to {@code expectedXmlContent} via 
	 * {@link XMLAssert#assertXMLEqual(String, Document, Document)}.
	 * <p>Use of this matcher requires
	 * <a href="http://xmlunit.sourceforge.net/"/>XMLUnit</a>.
	 */
	public ResultMatcher isEqualToXml(final String expectedXmlContent) {
		return new ServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				Document control = XMLUnit.buildControlDocument(expectedXmlContent);
				Document test = XMLUnit.buildTestDocument(response.getContentAsString());
				Diff diff = new Diff(control, test);
				if (!diff.similar()) {
					AssertionErrors.fail("Response content, " + diff.toString());
		        }				
			}
		};
	}
	
	/**
	 * Return a class with JsonPath result matchers.
	 */
	public JsonPathResultMatchers jsonPath(String jsonPath) {
		return new JsonPathResultMatchers(jsonPath);
	}
	
}
