package org.springframework.test.web.server.request;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.server.request.DefaultMockHttpServletRequestBuilder;
import org.springframework.util.FileCopyUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DefaultMockHttpServletRequestBuilderTests {

    private DefaultMockHttpServletRequestBuilder builder;

    private ServletContext servletContext;

    @Before
    public void setUp() throws Exception {
        builder = new DefaultMockHttpServletRequestBuilder(new URI("/foo"), HttpMethod.GET);
        servletContext = new MockServletContext();
    }

    @Test
    public void method() {
        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals("/foo", request.getRequestURI());
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void param() {
        builder.param("foo", "bar", "baz");

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        Map<String, String[]> parameterMap = request.getParameterMap();
        assertArrayEquals(new String[]{"bar", "baz"}, parameterMap.get("foo"));
    }

    @Test
    public void accept() throws Exception {
        builder.accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XML);

        MockHttpServletRequest request = builder.buildRequest(servletContext);

        List<String> accept = Collections.list(request.getHeaders("Accept"));
        assertEquals(2, accept.size());
        assertEquals("text/html", accept.get(0));
        assertEquals("application/xml", accept.get(1));
    }

    @Test
    public void contentType() throws Exception {
        builder.contentType(MediaType.TEXT_HTML);

        MockHttpServletRequest request = builder.buildRequest(servletContext);

        String contentType = request.getContentType();
        assertEquals("text/html", contentType);

        List<String> contentTypes = Collections.list(request.getHeaders("Content-Type"));
        assertEquals(1, contentTypes.size());
        assertEquals("text/html", contentTypes.get(0));
    }

    @Test
    public void body() throws Exception {
        byte[] body = "Hello World".getBytes("UTF-8");
        builder.body(body);

        MockHttpServletRequest request = builder.buildRequest(servletContext);

        byte[] result = FileCopyUtils.copyToByteArray(request.getInputStream());
        assertArrayEquals(body, result);
    }

    @Test
    public void header() throws Exception {
        builder.header("foo", "bar", "baz");

        MockHttpServletRequest request = builder.buildRequest(servletContext);

        List<String> headers = Collections.list(request.getHeaders("foo"));
        assertEquals(2, headers.size());
        assertEquals("bar", headers.get(0));
        assertEquals("baz", headers.get(1));
    }

    @Test
    public void cookie() throws Exception {
        Cookie cookie1 = new Cookie("foo", "bar");
        Cookie cookie2 = new Cookie("baz", "qux");
        builder.cookie(cookie1, cookie2);

        MockHttpServletRequest request = builder.buildRequest(servletContext);

        Cookie[] cookies = request.getCookies();
        assertEquals(2, cookies.length);
        assertEquals("foo", cookies[0].getName());
        assertEquals("bar", cookies[0].getValue());

        assertEquals("baz", cookies[1].getName());
        assertEquals("qux", cookies[1].getValue());
    }

    @Test
    public void locale() throws Exception {
        Locale locale = new Locale("nl", "nl");
        builder.locale(locale);

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals(locale, request.getLocale());
    }

    @Test
    public void characterEncoding() throws Exception {
        String encoding = "UTF-8";
        builder.characterEncoding(encoding);

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals(encoding, request.getCharacterEncoding());
    }

    @Test
    public void requestAttr() throws Exception {
        builder.requestAttr("foo", "bar");

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals("bar", request.getAttribute("foo"));
    }

    @Test
    public void sessionAttr() throws Exception {
        builder.sessionAttr("foo", "bar");

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals("bar", request.getSession().getAttribute("foo"));
    }

    @Test
    public void principal() throws Exception {
        Principal principal = new Principal() {
            public String getName() {
                return "Foo";
            }
        };
        builder.principal(principal);

        MockHttpServletRequest request = builder.buildRequest(servletContext);
        assertEquals(principal, request.getUserPrincipal());
    }
}
