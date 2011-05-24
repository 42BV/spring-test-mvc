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

package org.springframework.test.web.server.matcher;

import org.springframework.test.web.server.MvcResultMatcher;

/**
 * The {@link MvcResultMatchers}s in this class overlap with other {@code *Matchers} in this package.
 * The intent is to compile a list {@link MvcResultMatcher}s recommended for common use.
 * 
 */
public class MvcResultMatchers {

	public static MvcResultMatcher status(int status) {
		return MockResponseMatchers.status(status);
	}

	public static MvcResultMatcher contentType(String contentType) {
		return MockResponseMatchers.contentType(contentType);
	}

	public static MvcResultMatcher responseBody(String content) {
		return MockResponseMatchers.responseBody(content);
	}

	public static MvcResultMatcher responseBodyContains(String text) {
		return MockResponseMatchers.responseBodyContains(text);
	}

	public static MvcResultMatcher forwardedUrl(String forwardUrl) {
		return MockResponseMatchers.forwardedUrl(forwardUrl);
	}

	public static MvcResultMatcher redirectedUrl(String redirectUrl) {
		return MockResponseMatchers.redirectedUrl(redirectUrl);
	}

	public static MvcResultMatcher viewName(String viewName) {
		return ModelAndViewMatchers.viewName(viewName);
	}

	public static MvcResultMatcher noBindingErrors() {
		return ModelAndViewMatchers.noBindingErrors();
	}
	
	public static MvcResultMatcher modelAttributesHaveErrors(String...names) {
		return ModelAndViewMatchers.modelAttributesHaveErrors(names);
	}

	public static MvcResultMatcher modelAttributesPresent(String...names) {
		return ModelAndViewMatchers.modelAttributesPresent(names);
	}

	public static MvcResultMatcher loggingMatcher() {
		return new LoggingMatcher();
	}

}
