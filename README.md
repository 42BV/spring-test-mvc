Spring MVC Test Support
=======================

The goal of this project is to facilitate testing _Spring MVC_ controllers on the server side and _RestTemplate_ based code on the client side.

This code will be included in the `spring-test` module of the __Spring Framework__. Its present home here allows us to evolve it on a flexible release schedule and with community feedback potentially accommodating a wide range of scenarios.

Server-Side
===========

Overview
--------
Annotated-controllers depend on Spring MVC to handle many things such as mapping requests, performing data binding and validation, setting the response status, writing to the body of the response using the correct content type, and many more.

To test all that you may instantiate an in-memory Servlet container driving requests with _JWebUnit_ or you may use a test tool such as _JMeter_ or _Selenium_. These options are all valid. However they take longer to execute and can only perform black-box testing.

The aim of this project is to make it easy to test controllers by building on the familiar `MockHttpServletRequest` and the `MockHttpServletResponse` from the `spring-test` module and without the need for a Servlet container. Whether you want to point to one controller or to test with your complete web application context setup, it should be easy to send a request and verify the results.

Examples
--------

Test an `@ResponseBody` method in a controller:

    MockMvcBuilders.standaloneSetup(new TestController()).build()
        .perform(get("/form"))
            .andExpect(status().isOk())
            .andExpect(content().type("text/plain"))
            .andExpect(content().string("hello world"));

Test binding failure by pointing to Spring MVC XML-based context configuration:

    MockMvcBuilders.xmlConfigSetup("classpath:org/examples/servlet-context.xml").build()
        .perform(get("/form"))
	        .andExpect(status().isOk())
	        .andExpect(model().attributeHasErrors("formBean"))
	        .andExpect(view().name("form"));

Test serving a resource by pointing to Spring MVC Java-based application configuration:

    MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build()
        .perform(get("/resources/Spring.js"))
	        .andExpect(content().type("application/octet-stream"))
	        .andExpect(content().string(containsString("Spring={};")));

The last example uses a Hamcrest matcher to check if the content contains specific text.

Tips on Getting Started
-----------------------

See this [presentation](http://rstoyanchev.github.com/spring-31-and-mvc-test/#97).

There are many more examples in the [org.springframework.test.web.server.samples](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/server/samples) package.

The API is designed to be fluent and readable. Therefore to learn we recommend writing some tests and using code completion to discover what is available. 

Eclipse developers should add the following classes as "Favorites" under Preferences/Java/Editor/Content Assist: 
_MockMvcBuilders.*_, _MockMvcRequestBuilders.*_, _MockMvcResultMatchers.*_, and _MockMvcResultHandlers.*_. 

Now when you use _Ctrl+Space_, Eclipse will suggest matching static factory methods from those classes.

Limitations
-----------

Most rendering technologies should work as expected. For _Tiles_ and _JSP_, while you can test with your existing configuration as is, no actual JSP-based rendering will take place. Instead you can verify the path the request was forwarded to (i.e. the path to the JSP page) or you can also verify the selected view name.

Maven
=====

To get the first milestone release, use the SpringSource Artifactory `libs-milestone` repository:
http://repo.springsource.org/libs-milestone

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test-mvc</artifactId>
      <version>1.0.0.M1</version>
      <scope>test</scope>
    </dependency>

To get the latest snapshot (as well milestones), use the SpringSource Artifactory `libs-snapshot` repository:
http://repo.springsource.org/libs-snapshot

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test-mvc</artifactId>
      <version>1.0.0.BUILD-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>

Contributing
============

If you see anything you'd like to change we encourage taking advantage of github's social coding features by making the change in a [fork of this repository](http://help.github.com/forking/) and sending a pull request. 

To report an issue, use this project's [issue tracking](https://github.com/SpringSource/spring-test-mvc/issues?sort=updated&state=open).

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement] (https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.

License
=======

The Spring Test MVC project is available under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

Acknowledgements
================

This project draws inspiration from similar [server-side](http://static.springsource.org/spring-ws/sites/2.0/reference/html/server.html#d4e1487) and [client-side](http://static.springsource.org/spring-ws/sites/2.0/reference/html/client.html#d4e1860) test support introduced in Spring Web Services 2.0.




