# 2. Visible flow of control

Date: 2021-12-18

## Status

Accepted

## Context
Most today’s frameworks bind technology stacks automatically to your application core. If at all, you have to add a new 
dependency and rebuild the application. Unfortunately, you hide the flow of control which makes it harder for beginners 
to understand an application which is based on a ports and adapters architecture. This is especially true for the entry 
points of your application.

This might be obvious to incoming synchronous calls (RMI), but can be hard to see for incoming asynchronous messaging.
Most frameworks use annotations here, but the developer must be aware of them.


## Decision
* Jexxa uses explicit binding for driving adapters so that the main method represents the single starting point for 
 the flow of control.

* See tutorial [TimeService - Flow of Control](https://github.com/jexxa-projects/JexxaTutorials/blob/main/TimeService/README-FlowOfControl.md) for further information. 

## Consequences

* The main method includes some boilerplate code to bind required driving adapters to the application core.
* The Constructor of inbound ports only accept interfaces which are considered as outbound ports. Thus, the 
  implementation of an inbound port has to instantiate all required business objects by itself. Only outbound ports 
  are injected. 