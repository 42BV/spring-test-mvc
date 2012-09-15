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

package org.springframework.test.web.server.request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * A request builder for {@link MockMultipartHttpServletRequest}.
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 */
public class MultipartRequestBuilder extends DefaultRequestBuilder {

	private final List<MockMultipartFile> files = new ArrayList<MockMultipartFile>();


	/**
	 * Use {@link MockMvcRequestBuilders#fileUpload(String, Object...)} to
	 * obtain a new instance.
	 */
	protected MultipartRequestBuilder(URI uri) {
		super(uri, HttpMethod.POST);
		super.contentType(MediaType.MULTIPART_FORM_DATA);
	}

	/**
	 * Create a new MockMultipartFile with the given content.
	 *
	 * @param name    the name of the file
	 * @param content the content of the file
	 */
	public MultipartRequestBuilder file(String name, byte[] content) {
		files.add(new MockMultipartFile(name, content));
		return this;
	}

	/**
	 * Add the given MockMultipartFile.
	 *
	 * @param file the multipart file
	 */
	public MultipartRequestBuilder file(MockMultipartFile file) {
		files.add(file);
		return this;
	}

	@Override
	public Object merge(Object parent) {
		if (parent == null) {
			return this;
		}
		if (!(parent instanceof MultipartRequestBuilder)) {
			throw new IllegalArgumentException("Cannot merge with [" + parent.getClass().getName() + "]");
		}

		super.merge(parent);

		MultipartRequestBuilder parentBuilder = (MultipartRequestBuilder) parent;
		this.files.addAll(parentBuilder.files);

		return this;
	}

	@Override
	protected final MockHttpServletRequest createServletRequest(ServletContext servletContext) {
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		for (MockMultipartFile file : files) {
			request.addFile(file);
		}
		return request;
	}

}
