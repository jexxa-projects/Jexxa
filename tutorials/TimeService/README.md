# TimeService 

## What You Learn

* How to write an application service acting as a so called inbound-port 
* How to declare an outbound-port sending current time  
* How to provide an implementation of this outbound-port with console output
* How to provide an implementation of this outbound-port using `DrivenAdapterStrategy` from Jexxa for JMS.  

## What you need

*   Understand tutorial `HelloJexxa` because we explain only new aspects 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.3 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)

## Write the Application Core 

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

## Implement the Infrastructure

### Driven Adapter with console output 
The implementation is quite simple and just prints given time to a logger.  

Note: Jexxa uses implicit constructor injection together with a strict convention over configuration approach.

Therefore, each driven adapter needs one of the following constructors: 

*   Public Default constructor
*   Public constructor with a single `Properties` attribute
*   Public static factory method that gets no parameters and returns the type of the driving adapter
*   Public static factory method with a single `Properties` parameter and returns the type of the requested driving adapter
   
Since our driven adapter does not need/support any configuration parameter, we can uses Java's default constructor.   

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
That's it. 

### Driven Adapter with JMS

Jexxa provides so called `DrivenAdapterStrategy` for various Java-APIs such as JMS. When using these strategies the implementation of a driven adapter is just a facade and maps domain specific methods to the technology stack. In the following implementation we use the `JMSSender` provided by Jexxa.   

```java
public class JMSTimePublisher implements ITimePublisher
{
    private final JMSSender jmsSender;

    private static final String TIME_TOPIC = "TimeService";

    public JMSTimePublisher(Properties properties)
    {
        this.jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(LocalTime localTime)
    {
        jmsSender.sendToTopic(localTime.toString(), TIME_TOPIC);
    }
}
```
  

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
