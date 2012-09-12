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

import java.io.PrintStream;

import org.springframework.util.CollectionUtils;

/**
 * A {@code ValuePrinter} that writes to a {@link PrintStream}.
 *
 * @author Rossen Stoyanchev
 */
public class PrintStreamValuePrinter implements ValuePrinter {

	private final PrintStream printStream;

	public PrintStreamValuePrinter(PrintStream printStream) {
		this.printStream = printStream;
	}

	public void printHeading(String heading) {
		this.printStream.println();
		this.printStream.println(String.format("%20s:", heading));
	}

	public void printValue(String label, Object value) {
		if (value != null && value.getClass().isArray()) {
			value = CollectionUtils.arrayToList(value);
		}
		this.printStream.println(String.format("%20s = %s", label, value));
	}

}
