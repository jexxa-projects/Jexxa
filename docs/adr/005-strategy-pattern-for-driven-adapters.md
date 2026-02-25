# 5. Record architecture decisions

Date: 2021-12-23
Extended: 2026-02-25

## Status

Accepted

## Context

In a ports and adapters architecture, driven-adapters connect outbound ports of the application core to external technology stacks such as databases, message brokers, or other services. A naive implementation would couple the adapter directly to a specific technology, making it hard to replace or test in isolation.

Jexxa aims to keep driven adapters as simple as possible: ideally just a facade that maps between the outbound port API and a concrete technology API.

## Decision

Jexxa provides reusable, technology-specific implementations of common infrastructure patterns (e.g. repository, messaging) as interchangeable strategies. A driven adapter delegates to one of these strategies rather than implementing the infrastructure concern itself.

## Consequences

* Driven adapters become thin facades, reducing their complexity and the likelihood of bugs.
* The underlying technology (e.g. a specific database) becomes a plugin that can be replaced without touching the adapter or the application core.
* Driven adapters are easier to test in isolation, as strategies can be substituted with in-memory implementations.
* Developers must understand the Strategy design pattern to work effectively with driven adapters.
* The abstraction introduced by the strategy may obscure what is happening at the infrastructure level, which can make debugging harder for developers unfamiliar with the pattern.
