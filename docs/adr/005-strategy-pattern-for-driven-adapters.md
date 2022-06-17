# 5. Record architecture decisions

Date: 2021-12-23

## Status

Accepted

## Context

Jexxa is considered as a lightweight framework that ensures that junior developers get the time to learn the craftsmanship of software development, methods of software architecture and the domain language step by step from the intermediate and senior developers.

Because your senior and intermediate developers focus on the application core, your junior developers must focus on the technology stack. Therefore, the binding of the application core to a technology stack should be as simple as possible.

## Decision

Jexxa provides driven adapter strategies so that the implementation of driven adapters is just a simple facade, which maps between the API of outbound ports to corresponding API of the strategy.

## Consequences

* Regarding your business domain, your junior developers will learn at least the name of the most important business objects, because Aggregates include the business logic of this domain.

* From a software engineering point of view your junior developer gets familiar with the strategy design pattern.

* From an architectural point of view your junior developer gets familiar with the principal of dependency inversion.

* Finally, your developers learn that they can persist data within a database without thinking about the database layout. Using a strategy pattern instead makes the database to a plugin.

* As soon as your junior developers feel that they are not challenged with implementing driven adapters, give them one of the above points to study.