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

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides methods to define expectations on the HttpServletResponse content 
 * with XPath expressions.
 * 
 * @author Rossen Stoyanchev
 */
public class XpathResultMatchers {
	
	private final String expression;
	
	private final SimpleNamespaceContext namespaceContext;
	
	/**
	 * Protected constructor.
	 * @param expression the XPath expression to use
	 * @param namespaces namespaces used in the XPath expression, or {@code null}
	 * 
	 * @see MockMvcResultActions#response()
	 * @see ServletResponseResultMatchers#content()
	 * @see ContentResultMatchers#jsonPath(String)
	 */
	protected XpathResultMatchers(String expression, final Map<String, String> namespaces) {
		this.expression = expression;
		this.namespaceContext = new SimpleNamespaceContext();
		if (!CollectionUtils.isEmpty(namespaces)) {
			this.namespaceContext.setBindings(namespaces);
		}
	}

	/**
	 * Assert there is content at the underlying XPath path.
	 */
	public ResultMatcher exists() {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				Node node = applyXpath(response.getContentAsString(), XPathConstants.NODE, Node.class);
				AssertionErrors.assertTrue("No content for xpath: " + expression, node != null);
			}
		};
	}

	/**
	 * Assert there is no content at the underlying XPath path.
	 */
	public ResultMatcher doesNotExist() {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				Node node = applyXpath(response.getContentAsString(), XPathConstants.NODE, Node.class);
				AssertionErrors.assertTrue("Content found for xpath: " + expression, node == null);
			}
		};
	}

	/**
	 * Extract the content at the underlying XPath path and assert it equals 
	 * the given Object. This is a shortcut {@link #asText(Matcher)} with
	 * {@link Matchers#equalTo(Object)}.
	 */
	public ResultMatcher evaluatesTo(String expectedContent) {
		return asText(Matchers.equalTo(expectedContent));
	}
	
	/**
	 * Evaluate the content at the underlying XPath path as a String and assert it with 
	 * the given {@code Matcher<String>}.
	 * <p>Example:
	 * <pre>
	 * // Assumes static import of org.hamcrest.Matchers.equalTo
	 * 
	 * mockMvc.perform(get("/person/Patrick"))
	 *   .andExpect(response().content().xpath("/person/name/text()").evaluatesTo("Patrick"));
	 *  </pre>
	 */
	public ResultMatcher asText(final Matcher<String> matcher) {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				String result = applyXpath(response.getContentAsString(), XPathConstants.STRING, String.class);
				MatcherAssert.assertThat("Text for xpath: " + expression, result, matcher);
			}
		};
	}

	/**
	 * Evaluate the content at the underlying XPath path as a Number and
	 * assert it with the given {@code Matcher<Double>}.
	 */
	public ResultMatcher asNumber(final Matcher<Double> matcher) {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				double result = applyXpath(response.getContentAsString(), XPathConstants.NUMBER, double.class);
				MatcherAssert.assertThat("Number for xpath: " + expression, result, matcher);
			}
		};
	}

	/**
	 * Evaluate the content at the underlying XPath path as a Boolean and 
	 * assert it with the given {@code Matcher<Double>}.
	 */
	public ResultMatcher asBoolean(final Matcher<Boolean> matcher) {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				boolean result = applyXpath(response.getContentAsString(), XPathConstants.BOOLEAN, boolean.class);
				MatcherAssert.assertThat("Boolean for xpath: " + expression, result, matcher);
			}
		};
	}

	/**
	 * Evaluate the content at the underlying XPath path as a {@link NodeList}
	 * and assert the number of items in it.
	 */
	public ResultMatcher nodeCount(final int count) {
		return new AbstractServletResponseResultMatcher() {
			public void matchResponse(MockHttpServletResponse response) throws Exception {
				NodeList nodes = applyXpath(response.getContentAsString(), XPathConstants.NODESET, NodeList.class);
				AssertionErrors.assertEquals("Number of nodes for xpath: " + expression, nodes.getLength(), count);
			}
		};
	}

	/**
	 * Apply the underlying XPath to the given content. 
	 * @param <T> The expected return type (String, Double, Boolean, etc.)
	 * @param content the response content 
	 * @param evaluationType the type of evaluation to use
	 * @param returnType the expected return type
	 * @return the result of the evaluation
	 * @throws Exception if evaluation fails
	 */
	@SuppressWarnings("unchecked")
	protected <T> T applyXpath(String content, QName evaluationType, Class<T> returnType) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(this.namespaceContext);
		Document document = ResultMatcherUtils.toDocument(content);
		return (T) xpath.evaluate(this.expression, document, evaluationType);
	}

}
