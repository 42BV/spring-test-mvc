package org.springframework.test.web.server;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * A contract to build a {@link MockHttpServletRequest}.
 *
 * <p>See static factory methods in
 * {@code org.springframework.test.web.server.request.MockMvcRequestBuilders}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public interface RequestBuilder {

	/**
	 * Build the request.
	 *
	 * @param servletContext the {@link ServletContext} to use to create the request
	 *
	 * @return the request
	 */
    MockHttpServletRequest buildRequest(ServletContext servletContext);

}
