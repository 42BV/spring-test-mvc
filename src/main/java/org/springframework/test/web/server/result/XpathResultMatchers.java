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

import javax.xml.xpath.XPathExpressionException;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.support.XpathExpectationsHelper;
import org.w3c.dom.Node;

/**
 * 
 * TODO ...
 *
 * @author Rossen Stoyanchev
 */
public class XpathResultMatchers {

	private final XpathExpectationsHelper xpathHelper;

	public XpathResultMatchers(String expression, Map<String, String> namespaces, Object ... args) 
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
				XpathResultMatchers.this.xpathHelper.assertNode(content, matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher exists() {
		return node(Matchers.notNullValue());
	}

	/**
	 * TODO
	 */
	public ResultMatcher doesNotExist() {
		return node(Matchers.nullValue());
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher nodeCount(final Matcher<Integer> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				XpathResultMatchers.this.xpathHelper.assertNodeCount(content, matcher);
			}
		};
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher nodeCount(int count) {
		return nodeCount(Matchers.equalTo(count));
	}
	
	/**
	 * TODO
	 */
	public ResultMatcher string(final Matcher<? super String> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				XpathResultMatchers.this.xpathHelper.assertString(content, matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher string(String value) {
		return string(Matchers.equalTo(value));
	}

	/**
	 * TODO
	 */
	public ResultMatcher number(final Matcher<? super Double> matcher) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				XpathResultMatchers.this.xpathHelper.assertNumber(content, matcher);
			}
		};
	}

	/**
	 * TODO
	 */
	public ResultMatcher number(Double value) {
		return number(Matchers.equalTo(value));
	}

	/**
	 * TODO
	 */
	public ResultMatcher booleanValue(final Boolean value) {
		return new ResultMatcher() {
			public void match(MvcResult result) throws Exception {
				String content = result.getResponse().getContentAsString();
				XpathResultMatchers.this.xpathHelper.assertBoolean(content, value);
			}
		};
	}

}