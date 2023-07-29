[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa/jexxa)](https://maven-badges.herokuapp.com/maven-central/io.jexxa/jexxa/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![CodeQL](https://github.com/jexxa-projects/Jexxa/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/jexxa-projects/Jexxa/actions/workflows/codeql-analysis.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b6c1680824ef4ac5914c40073242dc86)](https://www.codacy.com/gh/repplix/Jexxa/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=repplix/Jexxa&amp;utm_campaign=Badge_Grade)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jexxa-projects_Jexxa&metric=alert_status)](https://sonarcloud.io/summary/overall?id=jexxa-projects_Jexxa)
 ![Java CI](https://github.com/jexxa-projects/Jexxa/workflows/Java%20CI/badge.svg)

# Jexxa - A Ports and Adapters Framework for Java
Jexxa is a lightweight framework to implement durable business applications based on a *ports and adapters* (aka _hexagonal_) architecture. *Ports and adapters* is a typical architecture style to structure microservices around your core business domains. 

Goal of this framework is to support the development of _durable_ business applications in conjunction with Domain Driven Design as good as possible. 
Following the UNIX philosophy `do one thing and do it well`, Jexxa is no general purpose framework such as Spring or Jakarta EE.
Therefore, Jexxa stresses the following aspects in particular:

*   **Technology-agnostic**: The framework is designed to avoid technology-specific dependencies in business applications. See [here](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html#_ioc_without_annotations) for more information.
*   **Visible flow of control**: Simplified navigation through your business application. Checkout [this tutorial](https://github.com/jexxa-projects/JexxaTutorials/blob/main/TimeService/README-FlowOfControl.md) for more information.
*   **Aligned team development**: The framework is tailored to the _needs and development of teams_ developing business applications. See [Jexxa's General Design Decisions](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html#_general_design_decisions) for more information. 

In addition, Jexxa offers following production-proven features:    

*   Integrated most common driving adapters: RMI over REST, and JMS. 
*   Integrated strategies for most common driven adapters: JDBC, in memory DB (IMDB), and JMS.
*   Integrated stubs to write unit-tests without mock frameworks.
*   Implementing typical microservice patterns such as transaction outbox sender.

## General information

*   Supported Java environments: ![Java](https://img.shields.io/badge/JDK-Java17+-blue.svg)

*   Documentation: 
    *   [Tutorials](https://github.com/jexxa-projects/JexxaTutorials) show typical use cases
    *   [A template](https://github.com/jexxa-projects/JexxaTemplate) for your first Jexxa application
    *   [Reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html) when you develop with Jexxa
    *   [Architecture of Jexxa](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html) to get a deep insight into Jexxa
    *   [Build Jexxa](docs/BUILD.md) in case you want to contribute 

*   Related Projects
    *   [Addend - Annotations for Domain Driven Design](https://github.com/jexxa-projects/Addend)
## Built With

Apart from some other great open source libraries, Jexxa mainly utilises the following libraries and frameworks:

*   [ClassGraph](https://github.com/classgraph/classgraph)
*   [javalin](http://javalin.io/)

## Quickstart

### Start programming 

Below, you see a simple ``Hello World`` example that is described in detail [here](https://github.com/jexxa-projects/JexxaTutorials/tree/main/HelloJexxa):  

```java     
public final class HelloJexxa
{
    // Our business logic ;-)
    public String greetings()                 { return "Hello Jexxa"; }

    public static void main(String[] args)    {
        //Create your jexxaMain for this application
        var jexxaMain = new JexxaMain(HelloJexxa.class);

        jexxaMain
                // Bind a REST adapter to expose parts of the application
                // Get greetings: http://localhost:7501/HelloJexxa/greetings
                .bind(RESTfulRPCAdapter.class).to(HelloJexxa.class)  
                  
                // Run Jexxa and all bindings until Ctrl-C is pressed
                .run();
    }
}
```    

### Add Dependencies
Whenever possible, Jexxa is developed against standard APIs. This allows a business application to use the preferred
technology stacks. Therefore, our `HelloJexxa` application needs two dependencies: `jexxa-web` and a logger that fulfills
your requirements, such as `slf4j-simple`. 

Maven:
```xml
<dependencies>
    <dependency>
       <groupId>io.jexxa</groupId>
       <artifactId>jexxa-web</artifactId>
       <version>6.1.4</version>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.7</version>
    </dependency>
</dependencies>
```

Gradle:

```groovy
compile "io.jexxa:jexxa-web:6.1.4"
compile "org.slf4j:slf4j-simple:2.0.7"
``` 

### Configure your Jexxa application  

By default, a Jexxa application looks for the following properties file. For more information please refer to the 
[reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_application_configuration). 

```maven
resources/jexxa-application.properties
```                                   

Available properties are described [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties).

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Copyright and license

Code and documentation copyright 2020–2023 Michael Repplinger. Code released under the [Apache 2.0 License](LICENSE). Docs released under [Creative Commons](https://creativecommons.org/licenses/by/3.0/).
