# BookStore 

## What You Learn

*   How to write an application service acting as a so called inbound-port 
*   How to declare an outbound-port sending current time  
*   How to provide an implementation of this outbound-port with console output
*   How to provide an implementation of this outbound-port using `DrivenAdapterStrategy` from Jexxa for JMS.  

## What you need

*   Understand tutorial `HelloJexxa` ans `TimeService` because we explain only new aspects 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.6 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)
*   curl or jconsole to trigger the application  

## 1. Implementing Application Core 



## 2. Implement the Infrastructure


### Driven Adapter with JMS

## 3. Implement the Application 

Finally, we have to write our application. As you can see in the code below there are two main differences compared to `HelloJexxa`:

*   We define the packages that should be used by Jexxa. This allows fine-grained control of used driven adapter since we must offer only a single implementation for each outbound port. In addition, this limits the search space for potential driven adapters and speeds up startup time.
*   We do not need to instantiate a TimeService class explicitly. This is done by Jexxa including instantiation of all required driven adapter.   
   
```java

```  

That's it. 

## Compile & Start the Application with console output 



