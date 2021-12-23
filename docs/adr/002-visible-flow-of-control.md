# 2. Visible flow of control

Date: 2021-12-18

## Status

Accepted

## Context


We need to visualize the flow of control to simplify the understanding of a large business application. Ideally the  
developer is guided through the business application on source code level as follows.  

*   `Driving Adapter` &rarr; `Inbound Port` &rarr; `Outbound Port` &rarr; `Driven Adapter`

## Decision

* The main method explicitly binds driving adapter to the application core. Even though this causes some boilerplate code
  which is tolerated because the main method represents the unique starting point of the flow of control.
* The attributes of the constructor of an inbound port are the outbound ports and must be interfaces. No other objects are
  accepted.   
* See tutorial [TimeService - Flow of Control](../../tutorials/TimeService/README-FlowOfControl.md) for further information. 

## Consequences

* The main method includes some boilerplate code to bind required driving adapters to the application core.
* The Constructor of inbound ports only accept interfaces which are considered as outbound ports. Thus, the 
  implementation of an inbound port has to instantiate all required business objects by itself. Only outbound ports 
  are injected. 