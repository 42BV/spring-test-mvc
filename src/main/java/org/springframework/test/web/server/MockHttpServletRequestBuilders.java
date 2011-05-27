package org.springframework.test.web.server;

import org.springframework.http.HttpMethod;

/** @author Arjen Poutsma */
public abstract class MockHttpServletRequestBuilders {

    private MockHttpServletRequestBuilders() {
    }

    public static DefaultMockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.GET, urlTemplate, urlVariables);
    }

    public static DefaultMockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.POST, urlTemplate, urlVariables);
    }

    public static DefaultMockHttpServletRequestBuilder put(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.PUT, urlTemplate, urlVariables);
    }

    public static DefaultMockHttpServletRequestBuilder delete(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.DELETE, urlTemplate, urlVariables);
    }

    public static MultipartMockHttpServletRequestBuilder fileUpload(String urlTemplate, Object... urlVariables) {
        return new MultipartMockHttpServletRequestBuilder();
    }

    public static DefaultMockHttpServletRequestBuilder request(HttpMethod method, String urlTemplate, Object... urlVariables) {
        return null;
    }



}
