# HelloJexxa

## What You Learn

* How to write a simple application using Jexxa
* How to bind different technology-stacks to the `BoundedContext` object
* How to access the running Jexxa-application from outside

## What you need

*   10 minutes
*   JDK 11 (or higher) installed 
*   Maven 3.3 (or higher) installed

## Compile & Start the Application

```console                                                          
mvn clean install
java -jar target/hellojexxa-jar-with-dependencies.jar
```
You will see following (or similar) output
```console
[main] INFO io.jexxa.core.JexxaMain - Start BoundedContext 'HelloJexxa' with 2 Driving Adapter 
[main] INFO org.eclipse.jetty.util.log - Logging initialized @446ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
[main] INFO io.javalin.Javalin - Javalin started in 194ms \o/
[main] INFO io.jexxa.core.JexxaMain - BoundedContext 'HelloJexxa' successfully started in 0.549 seconds
```

### Access the application via web browser
*   Get name of the bounded context:
    *   URL: http://localhost:7000/BoundedContext/contextName
    *   Result: 
    ```Json 
        HelloJexxa 
    ```
    
*   Get the uptime: 
    *   URL: http://localhost:7000/BoundedContext/uptime
    * Result: 
    ```Json 
        seconds	703
        units	
        0	"SECONDS"
        1	"NANOS"
        negative	false
        zero	false
        nano	807987000
    ```

### Access the application JConsole

*   Start jconsole and select the MBean `BoundedContext` as shown in screenshot below
*   Now you can execute all methods of this object 
*   Execute `shutdown` to end the application 

![JConsole](images/JConsole.png). 