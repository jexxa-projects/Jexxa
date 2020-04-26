[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/repplix/Jexxa.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/repplix/Jexxa/context:java)
 [![Total alerts](https://img.shields.io/lgtm/alerts/g/repplix/Jexxa.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/repplix/Jexxa/alerts/)
![Java CI](https://github.com/repplix/Jexxa/workflows/Java%20CI/badge.svg)

# Jexxa - A Ports and Adapters Framework for Java 

Jexxa is a lightweight framework to implement business applications based on a [ports and adapters](https://www.thinktocode.com/2018/07/19/ports-and-adapters-architecture/) architecture. 

Goal of this framework is to easily connect technology stacks to your technology agnostic business application. 

Up to now the framework is mainly used for educational purposes. Nevertheless it provides following features which allows writing lightweight business applications:
 
* Bind driving adapters to your application core within main-method.  

* Inject driven adapters into the application core without any framework specific @Annotations. 

* Integrated driving adapters: RMI over REST, JMX, and JMS. 

* Integrated driven adapters: JDBC, in memory DB (IMDB), and JMS based messaging. 

General information: 
* Project web page: [jexxa.io](https://www.jexxa.io)  
* Documentation: [Architecture of Jexxa](https://github.com/repplix/Jexxa/blob/master/doc/jexxa.adoc) 

## Quickstart

### Add dependency

#### Maven

```xml
<dependency>
  <groupId>io.ddd.Jexxa</groupId>
  <artifactId>Jexxa</artifactId>
  <version>1.3</version>
</dependency> 
```

#### Gradle

```groovy
compile "io.ddd.Jexxa:jexxa:1.3"
``` 

### Start programming 

A simple ``Hello World`` example which can be found [here](https://github.com/repplix/Jexxa/blob/master/src/test/java/io/ddd/jexxa/application/HelloJexxa.java):  

```java     
import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        //Create your jexxaMain for this application
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa");

        jexxaMain
                //Connect a JMX adapter to an object in order to access its public methods via `jconsole`
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                //Connect a REST adapter to an object in order to access its public methods via RMI over REST
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                //Start Jexxa and establish all connections
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
