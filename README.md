
Spring MVC Test Support
=======================

The goal of this project is to faciliate the creation of integration tests for _Spring MVC_ applications. At present it contains server-side support only but will have client-side support added as well.

This code is intended for inclusion in the _spring-test_ module of the __Spring Framework__. Its present home on _github/SpringSource_ allows us to evolve it initially on a more flexible schedule and with community feedback.

Server-Side
===========

Overview
--------
Annotated-controllers depend on Spring MVC to handle many things such as mapping requests, performing data binding and validation, setting the response status, writing to the body of the response using the correct content type, and many more.

To test all that you may instantiate an in-memory Servlet container driving requests with _JWebUnit_ or you may use a test tool such as _JMeter_ or _Selenium_. These options however require a running Servlet container and can only perform black-box testing.

The aim of this project is to provide a more "lightweight" and integrated alternative by building on the familiar `MockHttpServletRequest` and the `MockHttpServletResponse` from the _spring-test_ module and without the need for running a Servlet container. Whether you want to point to one controller or to test with your complete web application context setup, it should be easy to verify the results of sending requests.

Examples
--------

Test an `@ResponseBody` method in a controller:

    MockMvcBuilders.standaloneMvcSetup(new TestController()).build()
        .get("/form").execute()
            .andExpect(status(200))
            .andExpect(contentType("text/plain"))
	        .andExpect(responseBody("content"));

Test binding failure by pointing to Spring MVC XML-based context configuration:

    MockMvcBuilders.xmlConfigMvcSetup("classpath:org/examples/servlet-context.xml").build()
        .get("/form").execute()
            .andExpect(status(200))
            .andExpect(modelAttributesWithErrors("formBean"))
            .andExpect(viewName("form"));

Test serving a resource by pointing to Spring MVC Java-based application configuration:

    MockMvcBuilders.annotationConfigMvcSetup(TestConfiguration.class).build()
        .get("/resources/Spring.js").execute()
            .andExpect(contentType("application/octet-stream"))
            .andExpect(responseBodyContains("Spring={};"));

For more examples see tests in the `org.springframework.test.web.server` package.

Limitations
-----------

Most rendering technologies should work as expected. For _Tiles_ and _JSP_, while you can test with your existing configuration as is, no actual JSP-based rendering will take place. Instead you should verify the path the request was forwarded to (i.e. the path to the JSP page) or you can also verify the selected view name.

Contributions
=============

If you see anything you'd like to change we encourage taking advantage of github's social coding features by making the change in a [fork of this repository](http://help.github.com/forking/) and sending a pull request. 

To report an issue the Spring Framework forum or the Spring JIRA creating requests under the component _"SpringTEST"_.

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement] (https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.


