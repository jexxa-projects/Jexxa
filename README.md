[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa/jexxa)](https://maven-badges.herokuapp.com/maven-central/io.jexxa/jexxa/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![CodeQL](https://github.com/jexxa-projects/Jexxa/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/jexxa-projects/Jexxa/actions/workflows/codeql-analysis.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b6c1680824ef4ac5914c40073242dc86)](https://www.codacy.com/gh/repplix/Jexxa/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=repplix/Jexxa&amp;utm_campaign=Badge_Grade)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jexxa-projects_Jexxa&metric=alert_status)](https://sonarcloud.io/summary/overall?id=jexxa-projects_Jexxa)
 [![Java 25 LTS CI](https://github.com/jexxa-projects/Jexxa/actions/workflows/maven.yml/badge.svg)](https://github.com/jexxa-projects/Jexxa/actions/workflows/maven.yml)


# 🧩 Jexxa — A Ports & Adapters Framework for Java

**Jexxa** is a lightweight, opinionated framework to build durable, modular, and testable business applications using **Domain-Driven Design** and **Hexagonal Architecture** (Ports & Adapters).

> ⚠️ Jexxa is not a general-purpose framework like Spring — it’s focused solely on the business layer and clear system boundaries.


## 🚀 Key Features

- **Technology-Agnostic Core** — Clean separation of domain logic and infrastructure
- **Visible Control Flow** — Clear orchestration of application logic
- **Team-Friendly Design** — Scales well with cross-functional DDD teams
- **Built-In Adapters** — REST (RMI-style), JMS, JDBC, in-memory DB, S3-Storage
- **Resilient Microservice Patterns** — Includes transactional outbox, fail-fast startup combined with controlled retry strategies
- **Architecture Validation & Governance**
  — Enforces architectural rules and prevents erosion through automated validation

➡️ [Explore the Architecture](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html)
## 📋 Requirements

- Java **25 or higher**
- Maven-compatible IDE (e.g., IntelliJ IDEA, Eclipse)

## 🛠️ Quickstart

### Hello World Example

```java
public final class HelloJexxa {
    public String greetings() {
        return "Hello Jexxa";
    }

    static void main(String[] args) {
        var jexxaMain = new JexxaMain(HelloJexxa.class);

        jexxaMain
            .bind(RESTfulRPCAdapter.class).to(HelloJexxa.class)
            .run();
    }
}
```
* Access endpoint: http://localhost:7501/HelloJexxa/greetings
* See full example: [HelloJexxa Tutorial](https://github.com/jexxa-projects/JexxaTutorials/tree/main/HelloJexxa):


### Add Dependencies

Maven
```xml
<dependency>
    <groupId>io.jexxa</groupId>
    <artifactId>jexxa-web</artifactId>
    <version>9.0.5</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.17</version>
</dependency>
```

Gradle:

```groovy
compile "io.jexxa:jexxa-web:9.0.5"
compile "org.slf4j:slf4j-simple:2.0.17"
``` 
## ⚙️ Configuration
Jexxa expects the following configuration file in the classpath:

* View [example configuration](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-web/src/test/resources/jexxa-application.properties).
* [Configuration Reference](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_application_configuration).


## 📚 Ecosystem & Docs
*   [Jexxa-Tutorials](https://github.com/jexxa-projects/JexxaTutorials) — Sample projects and usage patterns
*   [JexxaArchetypes](https://github.com/jexxa-projects/JexxaArchetypes) — Maven archetypes for quick start
*   [Reference guide](https://jexxa-projects.github.io/Jexxa/jexxa_reference.html)
*   [Architecture Overview](https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html)
*   [Build Instructions](docs/BUILD.md)

## 🧩 Related Projects

* [Addend — Domain annotations for DDD](https://github.com/jexxa-projects/Addend)
* [JLegMed — Semantic bridging of legacy and modern systems](https://github.com/jexxa-projects/JLegMed)


## 🔨 Built With
*   [ClassGraph](https://github.com/classgraph/classgraph)
*   [javalin](http://javalin.io/)

## 🤝 Contributing

We ❤️ contributions!

If you want to propose changes or features:
* Open an issue to start a discussion
* Submit a PR with tests and updated documentation
* Questions or suggestions? Open an issue or start a GitHub Discussion

## 📜 License
* Source code: [Apache 2.0 License](LICENSE) - see [TLDR legal](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
* Documentation: [Creative Commons](https://creativecommons.org/licenses/by/4.0/)
* ©️ 2020–2026 Michael Repplinger
