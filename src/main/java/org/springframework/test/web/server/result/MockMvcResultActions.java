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

import org.springframework.test.web.server.ResultMatcher;
import org.springframework.test.web.server.ResultPrinter;

/**
 * A central class for access to all built-in {@link ResultMatcher}s and {@link ResultPrinter}s. 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public abstract class MockMvcResultActions {

	/**
	 * HttpServletRequest-related matchers.
	 */
	public static ServletRequestMatchers request() {
		return new ServletRequestMatchers();
	}

	/**
	 * HttpServletResponse-related matchers.
	 */
	public static ServletResponseMatchers response() {
		return new ServletResponseMatchers();
	}

	/**
	 * Handler and handler method-related matchers.
	 */
	public static HandlerMatchers handler() {
		return new HandlerMatchers();
	}

	/**
	 * Model-related matchers.
	 */
	public static ModelMatchers model() {
		return new ModelMatchers();
	}

	/**
	 * View-related matchers.
	 */
	public static ViewMatchers view() {
		return new ViewMatchers();
	}

	/**
	 * Console-based printer.
	 */
	public static ConsoleResultPrinter console() {
		return new ConsoleResultPrinter();
	}
	
}
