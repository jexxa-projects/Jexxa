# TimeService 

## What You Learn

*   How to write an application service acting as a so called inbound-port 
*   How to declare an outbound-port sending current time  
*   How to provide an implementation of this outbound-port with console output
*   How to provide an implementation of this outbound-port using `DrivenAdapterStrategy` from Jexxa for JMS.  

## What you need

*   Understand tutorial `HelloJexxa` because we explain only new aspects 
*   30 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.3 (or higher) installed
*   A running ActiveMQ instance (at least if you start the application with JMS)
*   curl or jconsoel to trigger the application  

## 1. Implement the Application Core 

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

## 2. Implement the Infrastructure

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

### Driven Adapter with JMS

Jexxa provides so called `DrivenAdapterStrategy` for various Java-APIs such as JMS. When using these strategies the implementation of a driven adapter is just a facade and maps domain specific methods to the technology stack. In the following implementation we use the `JMSSender` provided by Jexxa.   

Note: Since `JMSSender` requires information from a `Properties` we must provide a constructor or static factory method with a `Properties` attribute. By default, Jexxa hands in all information from jexxa-application.properties file.       

```java
public class JMSTimePublisher implements ITimePublisher
{
    private static final String TIME_TOPIC = "TimeService";

    private static final Logger LOGGER = JexxaLogger.getLogger(JMSTimePublisher.class);

    private final JMSSender jmsSender;
    
    public JMSTimePublisher(Properties properties)
    {
        this.jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(LocalTime localTime)
    {
        var localTimeAsString = localTime.toString();
        jmsSender.sendToTopic(localTimeAsString, TIME_TOPIC);
        LOGGER.info("Successfully published time {} to topic {}", localTimeAsString, TIME_TOPIC);
    }
}
```

Typically, information stated in `jexxa-application.properties` for JMS are as follows: 

```properties
#suppress inspection "UnusedProperty" for whole file
#Settings for JMSAdapter and JMSSender
java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory
java.naming.provider.url=tcp://localhost:61616
java.naming.user=admin
java.naming.password=admin
``` 

## 3. Implement the Application 

Finally, we have to write our application. As you can see in the code below there are two main differences compared to `HelloJexxa`:

*   We define the packages that should be used by Jexxa. This allows fine-grained control of used driven adapter since we must offer only a single implementation for each outbound port. In addition, this limits the search space for potential driven adapters and speeds up startup time.
*   We do not need to instantiate a TimeService class explicitly. This is done by Jexxa including instantiation of all required driven adapter.   
   
```java
public final class TimeServiceApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String JMS_DRIVEN_ADAPTER      = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.messaging";
    private static final String CONSOLE_DRIVEN_ADAPTER  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.console";
    private static final String OUTBOUND_PORTS          = TimeServiceApplication.class.getPackageName() + ".domainservice"; 

    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain("TimeService");

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                
                //Define which driven adapter should be used by Jexxa
                //Note: We can only register one driven adapter for the
                .addToInfrastructure(getDrivenAdapter(args))

                // Bind a REST and JMX adapter to the TimeService
                // It allows to access the public methods of the TimeService via RMI over REST or Jconsole
                .bind(RESTfulRPCAdapter.class).to(TimeService.class)
                .bind(JMXAdapter.class).to(TimeService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }
}
```  

That's it. 

## Compile & Start the Application with console output 

```console                                                          
mvn clean install
java -jar target/timeservice-jar-with-dependencies.jar 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'TimeService' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @644ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 121ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'TimeService' successfully started in 0.649 seconds
```          

### Publish the time 

You can use curl to publish the time.  
```Console
curl -X POST http://localhost:7000/TimeService/publishTime
```

Each time you execute curl you should see following output on console: 

```console                                                          
[qtp2095064787-31] INFO io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.console.ConsoleTimePublisher - 19:17:12.998278
```


## Compile & Start the Application with JMS 

```console                                                          
mvn clean install
java -jar target/timeservice-jar-with-dependencies.jar -j 
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'TimeService' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @644ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 121ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'TimeService' successfully started in 0.649 seconds
```          

### Publish the time
 
You can use curl to publish the time.  
```Console
curl -X POST http://localhost:7000/TimeService/publishTime
```

Each time you execute curl you should see following output on console: 

```console                                                          
[qtp26757919-34] INFO io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.messaging.JMSTimePublisher - Successfully published time 19:18:52.992826 to topic TimeService
```
