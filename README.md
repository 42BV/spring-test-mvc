
Spring MVC Test Support
-----------------------

The goal of this project is to faciliate the creation of integration tests for Spring MVC applications. At present it contains server-side test support only but will have client-side test support added as well.

Note that the code in this project is intended for inclusion in the "spring-test" module of the Spring Framework. Its present home on github/SpringSource allows us to evolve on a more flexible schedule and with community feedback.

Server-Side Test Support Overview
---------------------------------

Unit-testing controllers is easy. They're simple objects that can be invoked directly. Annotated-controllers however depend on much more that is handled by the framework such as mapping requests, performing data binding and validation, setting the response status, writing to the body of the response using the correct content type, and many others.

For integration-testing you can instantiate an in-memory Servlet container and drive requests with JWebUnit or you may create tests using tools like JMeter or Selenium. These options require a running Servlet container and can only perform black-box testing.

The Spring MVC test support aims to provide a more "lightweight" alternative by building on the MockHttpServletRequest and MockHttpServletResponse from spring-test, without the need for a running Servlet container. Whether you want to point to one controller or to use a full web application context setup, it should be easy to verify the results of sending a request.

Server-Side Test Examples
-------------------------

Test one annotated controller with @ResponseBody method:

standaloneSetup(new TestController()).buildServer()
    .get("/form").execute()
        .andExpect(status(200))
        .andExpect(contentType("text/plain"))
	    .andExpect(responseBody("content"));

Test using Java-based application configuration:

annotationConfigSetup(TestConfiguration.class).buildServer()
    .get("/form").execute()
        .andExpect(status(200))
        .andExpect(modelAttributesHaveErrors("formBean"))
        .andExpect(viewName("form"));

Test using XML context configuration:

xmlConfigSetup("classpath:org/examples/servlet-context.xml").buildServer()
    .get("/resources/Spring.js")
    .execute()
        .andExpect(contentType("application/octet-stream"))
        .andExpect(responseBodyContains("Spring={};"));

For more examples see tests in the org.springframework.test.web.server package.

Server-Side Test Limitations
----------------------------

Most rendering technologies should work as expected. 

While you can write test with you Tiles and JSP configuration as it is, no actual JSP compilation or rendering will take place. You can however verify the path (i.e. the JSP page) the request was forwarded to or the view name that was selected by the controller.

Contributions
-------------

If you see anything you'd like to change we encourage taking advantage of github's social coding features by making the change in a [fork of this repository](http://help.github.com/forking/) and sending a pull request. 

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement] (https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.


