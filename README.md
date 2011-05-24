
Spring MVC Test Support
-----------------------

The goal of this project is to faciliate the creation of integration tests for Spring MVC applications. At present it contains server-side support only but will have client-side support added as well.

This code is intended for inclusion in the "spring-test" module of the Spring Framework. Its present home on github/SpringSource allows us to evolve it initially on a more flexible schedule and with community feedback.

Server-Side Test Support Overview
---------------------------------

Annotated-controllers depend on Spring MVC to handle many things such as mapping requests, performing data binding and validation, setting the response status, writing to the body of the response using the correct content type, and many others.

To tests that you can instantiate an in-memory Servlet container and drive requests with JWebUnit or you may use a tool like JMeter or Selenium. These options require a running Servlet container and can only perform black-box testing.

This project aims to provide a more "lightweight" alternative building on the MockHttpServletRequest and MockHttpServletResponse from "spring-test", without the need for a running Servlet container. Whether you want to point to one controller or to a full web application context setup, it should be easy to verify the results of sending a request.

Server-Side Examples
--------------------

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
        .get("/resources/Spring.js").execute()
            .andExpect(contentType("application/octet-stream"))
            .andExpect(responseBodyContains("Spring={};"));

For more examples see tests in the org.springframework.test.web.server package.

Limitations
-----------

Most rendering technologies should work as expected. For Tiles and JSP, while you can run tests requests with your existing configuration as is, at present no actual JSP compilation or rendering will take place. Instead you can verify the path the request was forwarded to (i.e. the JSP page) or the view name that was selected by the controller.

Contributions
-------------

If you see anything you'd like to change we encourage taking advantage of github's social coding features by making the change in a [fork of this repository](http://help.github.com/forking/) and sending a pull request. 

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement] (https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.


