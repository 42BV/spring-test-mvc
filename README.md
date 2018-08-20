
This project facilitates testing _Spring MVC_ server-side and client-side _RestTemplate_-based code.

__NOTE: The project is now incorporated in the spring-test module of Spring Framework 3.2. Applications building against Spring Framework 3.1.x can continue to use this standalone project. However, applications building with Spring Framework 3.2 should use the spring-test module of Spring Framework 3.2 instead. See the Spring Framework [reference guide](http://static.springsource.org/spring-framework/docs/3.2.0.BUILD-SNAPSHOT/reference/htmlsingle/#spring-mvc-test-framework) for more details.__

To get started, see sample [server-side](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/server/samples) and [client-side](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/client/samples) tests. The [spring-mvc-showcase](https://github.com/SpringSource/spring-mvc-showcase) project also has many sample tests.

Milestone 2 can be obtained through the
http://repo.springsource.org/libs-milestone repository.

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test-mvc</artifactId>
      <version>1.0.0.M2</version>
      <scope>test</scope>
    </dependency>

The latest snapshot can be obtained through the http://repo.springsource.org/libs-snapshot repository.

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test-mvc</artifactId>
      <version>1.0.0.BUILD-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>

This project is available under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
