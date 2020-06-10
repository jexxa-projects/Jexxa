[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa/Jexxa)](https://maven-badges.herokuapp.com/maven-central/io.jexxa/Jexxa/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/repplix/Jexxa.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/repplix/Jexxa/context:java)
 [![Codacy Badge](https://api.codacy.com/project/badge/Grade/d5e41e143a3443a79b24b7b516ac5262)](https://app.codacy.com/manual/repplix/Jexxa?utm_source=github.com&utm_medium=referral&utm_content=repplix/Jexxa&utm_campaign=Badge_Grade_Dashboard)
 [![Total alerts](https://img.shields.io/lgtm/alerts/g/repplix/Jexxa.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/repplix/Jexxa/alerts/) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=repplix_Jexxa&metric=alert_status)](https://sonarcloud.io/dashboard?id=repplix_Jexxa)
 ![Java CI](https://github.com/repplix/Jexxa/workflows/Java%20CI/badge.svg)

# Jexxa - A Ports and Adapters Framework for Java 

Jexxa is a lightweight framework to implement business applications based on a [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/) architecture. 

Goal of this framework is to easily connect technology stacks to your technology agnostic business application. These connections are explicitly represented in the main-method of your application and can be independently exchanged.      

Even though Jexxa has strong educational focus it is used within lightweight business applications and microservices. At the moment it provides following features:
 
*   Inject driven adapters into the application core without any framework specific @Annotations. 
*   Bind driving adapters to your application core and expose its methods to remote clients.
*   Integrated driving adapters: RMI over REST, JMX, and JMS. 
*   Integrated strategies for driven adapters: JDBC, in memory DB (IMDB), and JMS based messaging. 

General information: 
*   Project web page: [jexxa.io](https://www.jexxa.io)  
*   Documentation: [Architecture of Jexxa](https://repplix.github.io/Jexxa/jexxa.html) 

## Supported Java environments
*   Java 11 (or higher)

## Quickstart

### Add dependency

#### Maven

```xml
<dependency>
  <groupId>io.jexxa</groupId>
  <artifactId>jexxa-core</artifactId>
  <version>2.0.0</version>
</dependency> 
```

#### Gradle

```groovy
compile "io.Jexxa:jexxa-core:2.0.0"
``` 

### Start programming 

A simple ``Hello World`` example can be found [here](https://github.com/repplix/Jexxa/blob/master/jexxa-samples/HelloJexxa/src/main/java/io/jexxa/sample/HelloJexxa.java):  

```java     
package io.jexxa.sample;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        //Create your jexxaMain for this application
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa");

        jexxaMain
                // Connect a JMX adapter to a business object.
                // It allows to access the public methods of the business object via `jconsole`
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                // Connect a REST adapter to same business object.
                // It allows to access the public methods of the business object via RMI over REST
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                //Start Jexxa and all bindings 
                .start()

                //Wait until shutdown is called by one of the following options:
                // - Press CTRL-C
                // - Use `jconsole` to connect to this application and invoke method shutdown
                // - Use HTTP-post to URL: `http://localhost:7000/BoundedContext/shutdown`
                //   (using curl: `curl -X POST http://localhost:7000/BoundedContext/shutdown`)
                .waitForShutdown()

                //Finally invoke stop() for proper cleanup
                .stop();
    }
}
```
### Adding a logger 
Jexxa does not include a logger, which means that you have to add your own logger to your application. If you do not add a logger, you will get a warning message to your console. In case your application has not any special requirements you can add the following dependency to your project:

```maven
 <dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-simple</artifactId>
   <version>1.7.30</version>
 </dependency>
```                                   

### Configure your Jexxa application  
By default, a JexxaMain instance looks for the following properties file
```maven
resources/jexxa-application.properties
```                                   

Available properties are described [here](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/main/resources/jexxa-application.properties)

## Build Jexxa from scratch

In case you would like to compile Jexxa by yourself without integration tests call: 

```maven
mvn clean install -DskipITs
```  

### Dependencies for integration tests 
For running integration tests we recommend using local docker containers to provide following dependencies:
*   An ActiveMQ instance with default settings: See [here](https://hub.docker.com/r/rmohr/activemq/).   
*   A PostgresDB database with default settings. Default user/password should be admin/admin: See [here](https://hub.docker.com/_/postgres).   
  
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
