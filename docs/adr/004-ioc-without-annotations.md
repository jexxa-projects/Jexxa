# 4. IoC without Annotations

Date: 2021-12-23

## Status

Accepted

## Context
Like any other framework, Jexxa takes control of part of your application core. 
Especially in Java, this is often done with framework-specific annotations. 
The downside is that these annotations tightly couple your application core 
to a specific technology stack.

## Decision
Jexxa does not use annotations for all IoC aspects used in the application core, such as dependency injection. 
Instead, conventions are used. 
To remember the essential conventions, they must be few and easy to remember. 
Thus, this decision is intended to support compliance with the KISS principle.

Note: Annotations are allowed between Jexxa and the infrastructure part of an application. For example, you can use 
annotations in the infrastructure part to map REST calls to an application service. 

## Consequences
* IoC concepts are not described by annotations inside the application core. Instead, the conventions must be known to the 
developers. To support this, Jexxa validates the conventions and shows appropriate error messages.   
* Dependency Injection must not use annotations. Therefore, Jexxa supports only implicit constructor injection. 

