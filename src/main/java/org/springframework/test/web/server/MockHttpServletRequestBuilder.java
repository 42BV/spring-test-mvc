package org.springframework.test.web.server;

import org.springframework.mock.web.MockHttpServletRequest;

/** @author Arjen Poutsma */
public interface MockHttpServletRequestBuilder {

    MockHttpServletRequest buildRequest();

}
