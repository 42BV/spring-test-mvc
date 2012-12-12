
This project facilitates testing _Spring MVC_ server-side and client-side _RestTemplate_-based code.

__NOTE: The project is now incorporated in the spring-test module of Spring Framework 3.2. Applications building against Spring Framework 3.1.x can continue to use this standalone project. However, applications building with Spring Framework 3.2 should use the spring-test module of Spring Framework 3.2. See the Spring Framework [reference guide] for more details.__

To get started, see sample [server-side tests](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/server/samples) and [client-side tests](spring-test-mvc/tree/master/src/test/java/org/springframework/test/web/client/samples). Also the [spring-mvc-showcase project](https://github.com/SpringSource/spring-mvc-showcase) has full test coverage using Spring MVC Test.

To get the second milestone release, use the SpringSource Artifactory `libs-milestone` repository:
http://repo.springsource.org/libs-milestone

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test-mvc</artifactId>
      <version>1.0.0.M2</version>
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

This project is available under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
