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

package org.springframework.test.web.server.result;

import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.XpathExpectationsHelper;
import org.w3c.dom.Node;

/**
 * Factory for response content {@code ResultMatcher}'s using an XPath
 * expression. An instance of this class is typically accessed via
 * {@code MockMvcResultMatchers.xpath(..)}.
 *
 * @author Rossen Stoyanchev
 */
public class XpathResultMatchers {

	private final XpathExpectationsHelper xpathHelper;


	/**
	 * Class constructor, not for direct instantiation. Use
	 * {@link MockMvcResultMatchers#xpath(String, Object...)} or
	 * {@link MockMvcResultMatchers#xpath(String, Map, Object...)}.
	 *
	 * @param expression the XPath expression
	 * @param namespaces XML namespaces referenced in the XPath expression, or {@code null}
	 * @param args arguments to parameterize the XPath expression with using the
	 * formatting specifiers defined in {@link String#format(String, Object...)}
	 *
	 * @throws XPathExpressionException
	 */
	protected XpathResultMatchers(String expression, Map<String, String> namespaces, Object ... args)
			throws XPathExpressionException {

		this.xpathHelper = new XpathExpectationsHelper(expression, namespaces, args);
	}

	/**
	 * Apply the XPath and assert it with the given {@code Matcher<Node>}.
	 */
	public ResultMatcher node(final Matcher<? super Node> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xpathHelper.assertNode(content, matcher);
			}
		};
	}

	/**
	 * Assert that content exists at the given XPath.
	 */
	public ResultMatcher exists() {
		return node(Matchers.notNullValue());
	}

	/**
	 * Assert that content does not exist at the given XPath.
	 */
	public ResultMatcher doesNotExist() {
		return node(Matchers.nullValue());
	}

	/**
	 * Apply the XPath and assert the number of nodes found with the given
	 * {@code Matcher<Integer>}.
	 */
	public ResultMatcher nodeCount(final Matcher<Integer> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xpathHelper.assertNodeCount(content, matcher);
			}
		};
	}

	/**
	 * Apply the XPath and assert the number of nodes found.
	 */
	public ResultMatcher nodeCount(int count) {
		return nodeCount(Matchers.equalTo(count));
	}

	/**
	 * Apply the XPath and assert the String content found with the given matcher.
	 */
	public ResultMatcher string(final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xpathHelper.assertString(content, matcher);
			}
		};
	}

	/**
	 * Apply the XPath and assert the String content found.
	 */
	public ResultMatcher string(String value) {
		return string(Matchers.equalTo(value));
	}

	/**
	 * Apply the XPath and assert the number of nodes found with the given matcher.
	 */
	public ResultMatcher number(final Matcher<? super Double> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xpathHelper.assertNumber(content, matcher);
			}
		};
	}

	/**
	 * Apply the XPath and assert the number of nodes found.
	 */
	public ResultMatcher number(Double value) {
		return number(Matchers.equalTo(value));
	}

	/**
	 * Apply the XPath and assert the boolean value found.
	 */
	public ResultMatcher booleanValue(final Boolean value) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				xpathHelper.assertBoolean(content, value);
			}
		};
	}

}