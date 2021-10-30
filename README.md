[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa/jexxa)](https://maven-badges.herokuapp.com/maven-central/io.jexxa/jexxa/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/repplix/Jexxa.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/repplix/Jexxa/context:java)
[![CI Badge](https://www.code-inspector.com/project/10009/score/svg)](https://frontend.code-inspector.com/public/project/10009/Jexxa/dashboard) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/d5e41e143a3443a79b24b7b516ac5262)](https://app.codacy.com/manual/repplix/Jexxa?utm_source=github.com&utm_medium=referral&utm_content=repplix/Jexxa&utm_campaign=Badge_Grade_Dashboard)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.jexxa%3Ajexxa&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.jexxa%3Ajexxa)
 ![Java CI](https://github.com/repplix/Jexxa/workflows/Java%20CI/badge.svg)

# Jexxa - A Ports and Adapters Framework for Java

Jexxa is a lightweight framework to implement durable business applications based on a [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/) architecture. It is particularly suitable for 
the implementation of microservices in conjunction with Domain Driven Design.

Goal of this framework is to support the development of _durable_ business applications as good as possible. Therefore, Jexxa stresses the following aspects in particular:

*   **Aligned development**: The framework is tailored to the needs of teams developing durable business applications. See [Jexxa's General Design Decisions](https://repplix.github.io/Jexxa/jexxa_architecture.html#_general_design_decisions) for more information. 
*   **Visible flow of control**: Simplified navigation through your business application. Checkout [this tutorial](tutorials/TimeService/README-FlowOfControl.md) for more information.      
*   **Technology agnostic**: IoC concepts such as dependency injection do not require any framework specific @Annotations. See [here](https://repplix.github.io/Jexxa/jexxa_architecture.html#_ioc_without_annotations) for more information.  
*   **Well-defined API**: Allows for the integration of arbitrary (old and new) technology stacks. 

In addition, Jexxa offers following production-proven features:    

*   Integrated most common driving adapters: RMI over REST, JMX, and JMS. 
*   Integrated strategies for most common driven adapters: JDBC, in memory DB (IMDB), and JMS. 
*   Integrated stubs for technology stacks to write unit-tests without mock frameworks.  

## General information

*   Supported Java environments: Java 11 (or higher)

*   Documentation: 
    *   [Tutorials](tutorials/README.md)
    *   [Reference guide](https://repplix.github.io/Jexxa/jexxa_reference.html)    
    *   [Architecture of Jexxa](https://repplix.github.io/Jexxa/jexxa_architecture.html)

## Built With

Apart from some other great open source libraries, Jexxa mainly utilises the following libraries and frameworks:

*   [ClassGraph](https://github.com/classgraph/classgraph)
*   [javalin](http://javalin.io/)

## Quickstart

### Download

Maven:
```xml
<dependency>
  <groupId>io.jexxa</groupId>
  <artifactId>jexxa-web</artifactId>
  <version>3.1.3</version>
</dependency> 
```

Gradle:

```groovy
compile "io.jexxa:jexxa-web:3.1.3"
``` 
 
### Start programming 

A simple ``Hello World`` example can be found [here](https://github.com/repplix/Jexxa/tree/master/tutorials/HelloJexxa):  

```java     
package io.jexxa.tutorials;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public final class HelloJexxa
{
    public String greetings()
    {
        return "Hello Jexxa";
    }

    public static void main(String[] args)
    {
        //Create your jexxaMain for this application
        var jexxaMain = new JexxaMain("HelloJexxa");

        jexxaMain
                // Bind a JMX adapter to our BoundedContext object.
                // It allows to access the public methods of the object via `jconsole`
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                // Bind a REST adapter to class HelloJexxa and expose its methods
                .bind(RESTfulRPCAdapter.class).to(HelloJexxa.class)

                //Start Jexxa and all bindings
                // - Open following URL in browser to get greetings: http://localhost:7500/HelloJexxa/greetings
                // - You can also use curl: `curl -X GET http://localhost:7500/HelloJexxa/greetings`
                .start()

                //Wait until shutdown is called by one of the following options:
                // - Press CTRL-C
                // - Use `jconsole` to connect to this application and invoke method shutdown
                .waitForShutdown()

                //Finally invoke stop() for proper cleanup
                .stop();
    }
}
```    

### Adding a logger
Whenever possible, Jexxa is developed against standard APIs. This allows a business application to use the preferred technology stacks. For further information please refer to [reference guide](https://repplix.github.io/Jexxa/jexxa_reference.html#_jexxa_modules).

Therefore, Jexxa does not include a logger, which means that you have to add your own logger to your application. If you do not add a logger, you will get a warning message to your console. In case your application has not any special requirements you can add the following dependency to your project:

Maven: 
```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-simple</artifactId>
  <version>1.7.32</version>
</dependency>
```                                   

Gradle:
```groovy
compile "org.slf4j:slf4j-simple:1.7.32"
``` 

### Configure your Jexxa application  

By default, a JexxaMain instance looks for the following properties file

```maven
resources/jexxa-application.properties
```                                   

Available properties are described [here](https://github.com/repplix/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties)

## Build Jexxa from scratch

In case you would like to compile Jexxa by yourself without integration tests call: 

```maven
mvn clean install -DskipITs
```  

### Dependencies for integration tests 

For running integration tests we recommend using local docker containers to provide following infrastructure:

*   An ActiveMQ instance with default settings: See [here](https://hub.docker.com/r/rmohr/activemq/).   
*   A PostgresDB database with default settings. Default user/password should be admin/admin: See [here](https://hub.docker.com/_/postgres).   

You can also use the docker stack provided [here](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/test/resources/DeveloperStack.yaml) 
  
Check the status of the running containers:

```docker
docker ps  -f status=running --format "{{.Names}}" 
```    

Output should look as follows

```docker
...
Postgres
activemq
...
```
  
To build Jexxa with integration tests call: 

```maven
mvn clean install 
```  

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Copyright and license

Code and documentation copyright 2020–2021 Michael Repplinger. Code released under the [Apache 2.0 License](LICENSE). Docs released under [Creative Commons](https://creativecommons.org/licenses/by/3.0/).