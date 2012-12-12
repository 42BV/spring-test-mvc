
The goal of this project is to facilitate testing _Spring MVC_ code on the server side and _RestTemplate_ based code on the client side.

__Note:__ The code in this project is now included in the _spring-test_ module of _Spring Framework 3.2_. See the Spring Framework [reference guide] for detailed documentation. Applications building against _Spring Framework 3.1.x_ can continue to use this standalone project. Applications building with _Spring Framework 3.2_ should use the _spring-test_ module of _Spring Framework 3.2_.

Tips on Getting Started
-----------------------

See sample [server-side tests](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/server/samples) and [client-side tests](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/client/samples). Also the [spring-mvc-showcase project](https://github.com/SpringSource/spring-mvc-showcase) has full test coverage using Spring MVC Test.

Maven
=====

To get the second milestone release, use the SpringSource Artifactory `libs-milestone` repository:
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

License
=======

The Spring MVC Test project is available under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

Acknowledgements
================

This project draws inspiration from similar [server-side](http://static.springsource.org/spring-ws/sites/2.0/reference/html/server.html#d4e1487) and [client-side](http://static.springsource.org/spring-ws/sites/2.0/reference/html/client.html#d4e1860) test support introduced in Spring Web Services 2.0.


