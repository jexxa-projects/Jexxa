# 6. Driven Adapters as a Learning Entry Point for Junior Developers

Date: 2026-02-25
## Status
Accepted

## Context

Jexxa is intended to support teams where junior developers are learning software craftsmanship alongside intermediate and senior developers. Senior developers focus on the application core and domain logic, while junior developers are assigned to the infrastructure layer, particularly driven adapters.

To be effective, the infrastructure layer must be simple enough for junior developers to implement confidently, while still exposing them to meaningful architectural and technical concepts.

## Decision
Driven adapters in Jexxa are deliberately kept simple (see ADR 005) so that junior developers can implement them without deep infrastructure knowledge. This simplicity is not incidental but a conscious design goal that shapes how Jexxa structures its infrastructure support.

## Consequences
* Junior developers encounter core business objects (Aggregates) through their work on driven adapters, and gradually learn the domain language of the application.
* They become familiar with the Strategy design pattern and the Dependency Inversion Principle in a practical context.
* They learn to persist data without needing to design a database schema, which lowers the barrier to contributing early.
* As their confidence grows, the complexity of their assignments can be increased incrementally — for example, by asking them to study the strategy implementations or contribute new ones.
* This ADR documents a team and process decision, not just a technical one. It should be revisited if the team composition or onboarding goals change significantly.

