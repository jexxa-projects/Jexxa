# TimeService - Flow of Control  

## What you learn

*   A general idea of the building blocks of a hexagonal architecture 
*   Follow the flow of control of your application using your architecture 
*   An initial understanding of [dependency inversion principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle) 

## What you need

*   Understand tutorial `HelloJexxa` and `TimeService` because we explain only new aspects 
*   30 minutes

## Building blocks of a hexagonal architecture

If you select ports and adapters as the architecture of your application, you have the following building blocks:

*   `Driving Adapter`: A driving adapter belongs to the infrastructure. It receives incoming requests from a client using a specific technology such as REST, RMI or, JMS and forwards it to the entry point of your business application called `inbound port`.
*   `Inbound Port`: An `inbound port` belongs to the application core and represents the use cases of your business application. If your business application grows, you can  apply the [interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle) to separate your `inbound ports` based on the clients you have.
*   `Outbound Port`: An `outbound port` is an interface that belongs to the application core. It describes required methods from an application core's point of view that can only be implemented by using a technology stack such as a logger or a database.
*   `Driven Adapter`: A `driven adapter` belongs to the infrastructure and implements a specific `outbound port` by using a concrete technology stack. 

Since this is a very high level abstraction, this is often called the **macro-architecture** of an application.

Fore more details please read the article [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/).
           
## Navigate through your application 

The first way to navigate through your source code that every developer learn is to follow the flow of control by lines of code. Most IDE's and debuggers support this very well. This works fine for tiny applications but will fail on large(r) projects. Here, you need another approach that scales independently of the lines of code. 

This is where the software architecture of an application comes into play. A suitable software architecture is the most scalable approach to navigate through your application. The main problem with software architecture is that it looks so simple and obvious on a white board but is quite 
hard to map to source code. Much worse, a missing understanding or misunderstanding of the software architecture can cause high developing costs in a long term.     

Let's see the flow of control through an application based on ports and adapters architecture:

*   `Driving Adapter` &rarr; `Inbound Port` &rarr; `Outbound Port` &rarr; `Driven Adapter`

This looks very simple but is not as easy to see in your source code. Therefore, Jexxa's API supports following the flow of control as good as possible. Let's see how it works...

### The main-method  

Each application starts with the main method. Therefore, it represents the first part which is`Driving Adapter` &rarr; `Inbound Port` which must be 
explicitly written within Jexxa.

```java
void main(String[] args)
{   
    ///...
    jexxaMain
        // Bind RESTfulRPCAdapter and JMXAdapter to TimeService class so that we can invoke its method
        .bind(RESTfulRPCAdapter.class).to(TimeService.class)
        .bind(JMXAdapter.class).to(TimeService.class);

        ///...
}
```
Now, we know following parts of our application: 

*   Used `driving adapters`: `RESTfulRPCAdapter` and `JMXAdapter`
*   Used `inbound ports`: `TimeService`

Please note that most large frameworks such as Spring or J2EE hide these aspects because it is seen as boiler plate code. You will see that this is true to a certain extend if you check Jexxa's tutorials. Anyway, it represents the starting point of our flow of control. Since it greatly simplifies the navigation through the application, we have to explicitly bind `driving adapters` to `inbound ports` within Jexxa. 

From above source code we can navigate into two different directions. Either we dive deep into a concrete `Driving Adapter` such as `RESTfulRPCAdapter`. Or we follow direction `Inbound Port` &rarr; `Outbound Port` by selecting `TimeService` and enter the application core.  

### Enter the application core

If we select an inbound port such as `TimeService`, the constructor looks as follows. 

```java
public TimeService(ITimePublisher timePublisher, IMessageDisplay messageDisplay)
{
  // ...
}
```

From an architectural point of view, the constructor represents `Inbound Port` &rarr; `Outbound Port`. Within Jexxa, all parameters of the constructor must be `outbound ports`. These are the only parameters that are essential to create an `inbound port`. If you need any other parameters in the constructor of your `inbound port` it is most likely that you have an issue in your software design. 

Obviously, the constructor of an `inbound port` should only take these `outbound ports` that are required for its use cases. Even in a large application, the constructor should take only a few parameters. Otherwise, you should think about splitting your `inbound port`.
Especially in a large application, you automatically fade out a lot of source code.

In this example we know following of our application:
*   Current `inbound port`: `TimeService`
*   Required `outbound ports`: `ITimePublisher` and `IMessageDisplay`

From this point we can navigate into two different directions again. Either we dive deep into the application core by checking the implementation of `TimeService`. Please note that within a large application core you use a so-called micro architecture that supports the navigation through your application core. Please check tutorial [BookStore](https://github.com/repplix/Jexxa/tree/master/tutorials/BookStore) to see a potential mapping of an onion architecture for your application core.   

Alternatively, you can select one of the `Outbound ports` from your IDE to continue into direction `Outbound Port` &rarr; `Driven Adapter`.
   
### Leave the application core  

If we select `IMessageDisplay` we see just an interface, as described in the beginning. 

```java
public interface IMessageDisplay
{                                       
  void show(String message);
}
```
This interface describes required methods from a domain's point of view that can only be implemented by using a technology stack such as a logger or a database. 

The most important aspect here is the following: 

Our flow of control states the direction `Outbound Port` &rarr; `Driven Adapter`. But an `Outbound Port` is a much higher abstraction that must not depend on a specific infrastructure. So the direction of the dependency must be `Outbound Port` &larr; `Driven Adapter` which is done by using [dependency inversion principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle). For this purpose any object-oriented language uses the concept of an `interface`. Therefore, we must declare a high-level interface that belongs to our application core. This interface is then implemented by a `driven adapter` which again belongs to the infrastructure.  

This approach ensures that we can easily exchange the technology stack that is used by our application core. From this point we can use hot-keys of our IDE to switch to the concrete implementation of the interface which is located in the infrastructure part again. In this application the implementation is quite simple. 

```java
public class MessageDisplay implements IMessageDisplay
{
    @Override
    public void show(String message)
    {
        JexxaLogger.getLogger(MessageDisplay.class).info(message);
    }
}
```

## Summary

I hope you get an idea how to easily navigate through your application using the underlying architecture.  
