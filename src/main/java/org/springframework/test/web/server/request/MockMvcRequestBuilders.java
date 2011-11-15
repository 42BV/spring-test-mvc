package org.springframework.test.web.server.request;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.test.web.server.RequestBuilder;
import org.springframework.web.util.UriTemplate;

/** 
 * The main class to import to access all available {@link RequestBuilder}s.
 * 
 * <p><strong>Eclipse users:</strong> you can add this class as a Java editor 
 * favorite. To navigate, open the Preferences and type "favorites".
 * 
 * @author Arjen Poutsma 
 * @author Rossen Stoyanchev
 */
public abstract class MockMvcRequestBuilders {

    private MockMvcRequestBuilders() {
    }

    public static DefaultRequestBuilder get(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.GET, urlTemplate, urlVariables);
    }

    public static DefaultRequestBuilder post(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.POST, urlTemplate, urlVariables);
    }

    public static DefaultRequestBuilder put(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.PUT, urlTemplate, urlVariables);
    }

    public static DefaultRequestBuilder delete(String urlTemplate, Object... urlVariables) {
        return request(HttpMethod.DELETE, urlTemplate, urlVariables);
    }

    public static MultipartRequestBuilder fileUpload(String urlTemplate, Object... urlVariables) {
        URI url = expandUrl(urlTemplate, urlVariables);
        return new MultipartRequestBuilder(url);
    }

    public static DefaultRequestBuilder request(HttpMethod method, String urlTemplate, Object... urlVariables) {
        URI url = expandUrl(urlTemplate, urlVariables);
        return new DefaultRequestBuilder(url, method);
    }

    private static URI expandUrl(String urlTemplate, Object[] urlVariables) {
        UriTemplate uriTemplate = new UriTemplate(urlTemplate);
        return uriTemplate.expand(urlVariables);
    }


}
