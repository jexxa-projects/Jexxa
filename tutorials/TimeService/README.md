# TimeService 

## What You Learn

* How to write an application service acting as a so called inbound-port 
* How to declare an outbound-port sending current time  
* How to provide two different implementations, or so called driving adapters, of this outbound-port. One driving adapter uses console output. The other one uses JMS.  

## What you need

*   Understand tutorial `HelloJexxa` 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.3 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)

## Write the Application 

### Implement class `TimeService` 

This class provides the supports the main two use cases of this application which are: 
*   Provide current time
*   Publish current time in any way.   

The most important aspect here is that a technology-agnostic application must not use any technology-stack. Therefore, 
we must define an interface `ITimePublisher` that provides us the possibility to publish the time.   

```java
public class TimeService
{
    private final ITimePublisher timePublisher;

    public TimeService(ITimePublisher timePublisher)
    {
        this.timePublisher = timePublisher;
    }

    public LocalTime getTime()
    {
        return LocalTime.now();
    }
    
    public void publishTime()
    {
        timePublisher.publish(getTime());
    }
}
```                  

### Declare interface `ITimePublisher`

The interface is quite simple since we need just a single method to publish a time. 

```java
public interface ITimePublisher
{
    void publish(LocalTime localTime);
}
```                  

### Implement the driven adapter for interface `ITimePublisher`

#### Driven Adapter with console output 
The implementation is quite simple and just prints given time to a logger. That's it.  

Note: Jexxa uses implicit constructor injection together with a strict convention over configuration approach.

Therefore, each driven adapter needs one of the following constructors: 
* Public Default constructor
* Public constructor with a single `Properties` attribute
* Public static factory method that gets no parameters and returns the type of the driving adapter
* Public static factory method with a single `Properties` parameter and returns the type of the requested driving adapter
   

```java
public class ConsoleTimePublisher implements ITimePublisher
{

    private static final Logger LOGGER = JexxaLogger.getLogger(ConsoleTimePublisher.class);

    @Override
    public void publish(LocalTime localTime)
    {
        Validate.notNull(localTime);

        var logMessage = localTime.toString();

        LOGGER.info(logMessage);
    }
}
```

#### Driven Adapter with JMS
 
 

## Compile & Start the Application

```console                                                          
mvn clean install
java -jar target/timeservice-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'TimeServiceApplication' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @440ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 129ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'TimeServiceApplication' successfully started in 0.483 seconds
```
