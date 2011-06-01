package org.springframework.test.web.server;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;

/** @author Arjen Poutsma */
public interface MockHttpServletRequestBuilder {

    MockHttpServletRequest buildRequest(ServletContext servletContext);

}
