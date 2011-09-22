package org.springframework.test.web.server.result;


import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

public abstract class MockHttpServletResponseResultMatcher implements ResultMatcher {

    public final void match(MockHttpServletRequest request,
            MockHttpServletResponse response,
            Object handler,
            HandlerInterceptor[] interceptors,
            ModelAndView mav,
            Exception resolvedException) {

        try {
            matchResponse(response);
        }
        catch (IOException e) {
            e.printStackTrace();
            AssertionErrors.fail("Failed mock response expectation: " + e.getMessage());
        }
    }

    protected abstract void matchResponse(MockHttpServletResponse response) throws IOException;
}
