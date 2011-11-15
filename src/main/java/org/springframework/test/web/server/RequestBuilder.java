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

    MockHttpServletRequest buildRequest(ServletContext servletContext);

}
