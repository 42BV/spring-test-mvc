package org.springframework.test.web.server.result;

import java.io.IOException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base class for Matchers that assert the HttpServletResponse.
 */
public abstract class AbstractServletResponseResultMatcher implements ResultMatcher {

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
