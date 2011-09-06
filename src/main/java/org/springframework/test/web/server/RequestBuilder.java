package org.springframework.test.web.server;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;

/** 
 * A contract for building a {@link MockHttpServletRequest}.
 * 
 * <p>Access all available request builders through:
 * {@code org.springframework.test.web.server.request.MockMvcRequestBuilders}.
 * 
 * @author Arjen Poutsma 
 * @author Rossen Stoyanchev
 */
public interface RequestBuilder {

    MockHttpServletRequest buildRequest(ServletContext servletContext);

}
