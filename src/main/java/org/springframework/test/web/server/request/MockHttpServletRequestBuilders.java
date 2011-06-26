package org.springframework.test.web.server.request;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriTemplate;

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
        URI url = expandUrl(urlTemplate, urlVariables);
        return new MultipartMockHttpServletRequestBuilder(url);
    }

    public static DefaultMockHttpServletRequestBuilder request(HttpMethod method, String urlTemplate, Object... urlVariables) {
        URI url = expandUrl(urlTemplate, urlVariables);
        return new DefaultMockHttpServletRequestBuilder(url, method);
    }

    private static URI expandUrl(String urlTemplate, Object[] urlVariables) {
        UriTemplate uriTemplate = new UriTemplate(urlTemplate);
        return uriTemplate.expand(urlVariables);
    }


}
