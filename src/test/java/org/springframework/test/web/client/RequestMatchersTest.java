package org.springframework.test.web.client;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class RequestMatchersTest {

	private MockClientHttpRequest request;

	@Before
	public void setUp() {
		request = new MockClientHttpRequest();
	}

	@Test
	public void requestTo() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		assertMatch(RequestMatchers.requestTo("http://foo.com/bar"));
	}

	@Test(expected=AssertionError.class)
	public void requestTo_doesNotMatch() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		assertMatch(RequestMatchers.requestTo("http://foo.com/wrong"));
	}

	@Test
	public void requestToContains() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		assertMatch(RequestMatchers.requestToContains("bar"));
	}

	@Test
	public void requestToContains_doesNotContain() throws Exception {
		request.setUri(new URI("http://foo.com/bar"));

		assertMatch(RequestMatchers.requestToContains("baz"));
	}

	@Test
	public void method() throws Exception {
		request.setHttpMethod(HttpMethod.GET);

		assertMatch(RequestMatchers.method(HttpMethod.GET));
	}

	@Test(expected=AssertionError.class)
	public void method_doesNotMatch() throws Exception {
		request.setHttpMethod(HttpMethod.POST);

		assertMatch(RequestMatchers.method(HttpMethod.GET));
	}

	@Test
	public void header() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar", "baz");

		assertMatch(RequestMatchers.header("foo", "bar"));
		assertMatch(RequestMatchers.header("foo", "baz"));
	}

	@Test(expected=AssertionError.class)
	public void header_withMissingHeader() throws Exception {
		assertMatch(RequestMatchers.header("foo", "bar"));
	}

	@Test(expected=AssertionError.class)
	public void header_withMissingValue() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar", "baz");

		assertMatch(RequestMatchers.header("foo", "bad"));
	}

	@Test
	public void headerContains() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar", "baz");

		assertMatch(RequestMatchers.headerContains("foo", "ba"));
	}

	@Test(expected=AssertionError.class)
	public void headerContains_withMissingHeader() throws Exception {
		assertMatch(RequestMatchers.headerContains("foo", "baz"));
	}

	@Test(expected=AssertionError.class)
	public void headerContains_withMissingValue() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar", "baz");

		assertMatch(RequestMatchers.headerContains("foo", "bx"));
	}

	@Test
	public void headers() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar", "baz");

		HttpHeaders headers = new HttpHeaders();
		addToHeaders(headers, "foo", "bar", "baz");

		assertMatch(RequestMatchers.headers(headers));
	}

	@Test(expected=AssertionError.class)
	public void headers_withMissingHeader() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		addToHeaders(headers, "foo", "bar");

		assertMatch(RequestMatchers.headers(headers));
	}

	@Test(expected=AssertionError.class)
	public void headers_withMissingValue() throws Exception {
		addToHeaders(request.getHeaders(), "foo", "bar");

		HttpHeaders headers = new HttpHeaders();
		addToHeaders(headers, "foo", "bar", "baz");

		assertMatch(RequestMatchers.headers(headers));
	}

	@Test
	public void body() throws Exception {
		writeToRequestBody("test");

		assertMatch(RequestMatchers.body("test"));
	}

	@Test(expected=AssertionError.class)
	public void body_notEqual() throws Exception {
		writeToRequestBody("test");

		assertMatch(RequestMatchers.body("Test"));
	}

	private void assertMatch(RequestMatcher matcher) throws IOException {
		matcher.match(request);
	}

	private void addToHeaders(HttpHeaders headers, String name, String... values) {
		headers.put(name, Arrays.asList(values));
	}

	private void writeToRequestBody(String text) throws IOException {
		request.getBody().write(text.getBytes());
	}
}
