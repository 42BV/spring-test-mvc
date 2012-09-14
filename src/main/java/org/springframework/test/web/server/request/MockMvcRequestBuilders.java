package org.springframework.test.web.server.request;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.test.web.server.RequestBuilder;
import org.springframework.web.util.UriTemplate;

/**
 * Static factory methods for {@link RequestBuilder}s.
 *
 * <p><strong>Eclipse users:</strong> consider adding this class as a Java
 * editor favorite. To navigate, open the Preferences and type "favorites".
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public abstract class MockMvcRequestBuilders {

	private MockMvcRequestBuilders() {
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for a GET request.
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	public static DefaultRequestBuilder get(String urlTemplate, Object... urlVariables) {
		return request(HttpMethod.GET, urlTemplate, urlVariables);
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for a POST request.
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	public static DefaultRequestBuilder post(String urlTemplate, Object... urlVariables) {
		return request(HttpMethod.POST, urlTemplate, urlVariables);
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for a PUT request.
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	public static DefaultRequestBuilder put(String urlTemplate, Object... urlVariables) {
		return request(HttpMethod.PUT, urlTemplate, urlVariables);
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for a DELETE request.
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	public static DefaultRequestBuilder delete(String urlTemplate, Object... urlVariables) {
		return request(HttpMethod.DELETE, urlTemplate, urlVariables);
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for a multipart request.
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	public static MultipartRequestBuilder fileUpload(String urlTemplate, Object... urlVariables) {
		URI url = expandUrl(urlTemplate, urlVariables);
		return new MultipartRequestBuilder(url);
	}

	/**
	 * Create a {@link DefaultRequestBuilder} for any HTTP method.
	 * @param httpMethod the HTTP method
	 * @param urlTemplate a URI template including any component (e.g. scheme, host, query)
	 * @param urlVariables zero or more URI variables
	 */
	private static DefaultRequestBuilder request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
		URI url = expandUrl(urlTemplate, urlVariables);
		return new DefaultRequestBuilder(url, httpMethod);
	}

	private static URI expandUrl(String urlTemplate, Object[] urlVariables) {
		UriTemplate uriTemplate = new UriTemplate(urlTemplate);
		return uriTemplate.expand(urlVariables);
	}

}
