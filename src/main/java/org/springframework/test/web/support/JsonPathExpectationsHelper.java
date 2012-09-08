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

package org.springframework.test.web.support;

import static org.springframework.test.web.AssertionErrors.assertTrue;

import java.text.ParseException;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import com.jayway.jsonpath.JsonPath;

/**
 * A helper class for applying assertions using JSONPath expressions.
 *
 * @author Rossen Stoyanchev
 */
public class JsonPathExpectationsHelper {

	private final String expression;

	private final JsonPath jsonPath;


	/**
	 * Class constructor.
	 *
	 * @param expression the JSONPath expression
	 * @param args arguments to parameterize the JSONPath expression with using the
	 * formatting specifiers defined in {@link String#format(String, Object...)}
	 */
	public JsonPathExpectationsHelper(String expression, Object ... args) {
		this.expression = String.format(expression, args);
		this.jsonPath = JsonPath.compile(this.expression);
	}

	/**
	 * Evaluate the JSONPath and assert the resulting value with the given {@code Matcher}.
	 */
	@SuppressWarnings("unchecked")
	public <T> void assertValue(String content, Matcher<T> matcher) throws ParseException {
		T value = (T) evaluateJsonPath(content);
		MatcherAssert.assertThat("JSON path: " + expression, value, matcher);
	}

	private Object evaluateJsonPath(String content) throws ParseException  {
		return this.jsonPath.read(content);
	}

	/**
	 * Apply the JSONPath and assert the resulting value.
	 */
	public void assertValue(Object value) throws ParseException {
		assertValue(Matchers.equalTo(value));
	}

	/**
	 * Evaluate the JSON path and assert the resulting content exists.
	 */
	public void exists(String content) throws ParseException {
		Object value = evaluateJsonPath(content);
		String reason = "No value for JSON path: " + expression;
		assertTrue(reason, value != null);
		if (List.class.isInstance(value)) {
			assertTrue(reason, !((List<?>) value).isEmpty());
		}
	}

	/**
	 * Evaluate the JSON path and assert it doesn't point to any content.
	 */
	public void doesNotExist(String content) throws ParseException {
		Object value = evaluateJsonPath(content);
		String reason = String.format("Expected no value for JSON path: %s but found: %s", expression, value);
		if (List.class.isInstance(value)) {
			assertTrue(reason, ((List<?>) value).isEmpty());
		}
		else {
			assertTrue(reason, value == null);
		}
	}

}
