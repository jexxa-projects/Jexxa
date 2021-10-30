# TimeService - Flow of Control #

## What you learn ##

*   A general idea of the building blocks of ports and adapters 
*   How to follow the flow of control of your application using your architecture 
*   An initial understanding of [dependency inversion principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle) 

## What you need ##

*   Understand tutorial `HelloJexxa` and `TimeService` because we explain only new aspects 
*   45 minutes (take the time)

## Building blocks of a hexagonal architecture ##

If you choose ports and adapters as the architecture of your application, you have the following building blocks:

*   `Driving Adapter`: A driving adapter belongs to the infrastructure. It receives incoming requests from a client using a specific technology such 
    as REST, RMI or, JMS. Then it forwards the request to an `inbound port` for execution and this *drives* the domain logic of your application.
    
*   `Inbound Port`: An `inbound port` belongs to the application core and represents the use cases of your business application that can be started 
    by a specific client or a group of clients via an `Driving Adapter`. 
    
*   `Outbound Port`: An `outbound port` is an interface that belongs to the application core. It describes required methods from an application core's point of view that can only be implemented by using a technology stack such as a logger or a database.
    
*   `Driven Adapter`: A `driven adapter` belongs to the infrastructure and implements a specific `outbound port` by using a concrete technology stack such as database. This building block is *driven* by the domain logic of the application.   

Since this is a very high level abstraction, this architecture is often called the **macro-architecture** of an application.

Fore more details please read the article [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/).
           
## Navigate through your application ##

The first way to navigate through source code that every developer learns is to follow the lines of code. This allows you to see the flow of control,
which eventually allows you to understand the application logic. Most IDE's and debuggers support this very well. However, this only works well for a
limited number of lines of code and/or for a few possible paths. For larger applications, you need an approach that scales independently of 
the lines of code and excludes a large portion of possible paths directly.

This is where the software architecture of an application comes into play. A suitable software architecture is the most scalable approach to navigate 
through your application. The main problem with software architecture is that it looks so simple and obvious on a white board but is quite 
hard to see in the source code. Much worse, a missing understanding or misunderstanding of the software architecture can cause high developing costs 
in a long term.     

Let's see the flow of control of an incoming command through an application based on ports and adapters architecture:

*   `Driving Adapter` &rarr; `Inbound Port` &rarr; `Outbound Port` &rarr; `Driven Adapter` 
    
Then the command returns (from right to left):

*   `Driving Adapter` &larr; `Inbound Port` &larr; `Outbound Port` &larr; `Driven Adapter` 

This looks very simple but is not as easy to see in the source code. Therefore, Jexxa's API and the used conventions support following the flow of 
control as good as possible. 

Let's see how it works...

### The main-method ###

Each application starts with the `main` method. Since it is our starting point, it should represent the beginning of the flow of control which is
`Driving Adapter` &rarr; `Inbound Port`. For this purpose Jexxa's API offers methods to represent this binding explicitly in the main method.

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

*   Used `Driving Adapter`: `RESTfulRPCAdapter` and `JMXAdapter`
*   Used `Inbound Port`: `TimeService`

Please note that frameworks such as Spring or J2EE allow to hide this step because it is seen as boilerplate code. You will see that 
this is true to a certain extent if you check Jexxa's tutorials. Especially the main-class which includes this step is quite similar. 
Anyway, please remember the quote `code is written once but read many times`. Since the main-class represents our starting point and 
greatly simplifies the navigation through the application, it is worth the effort. 

Based on this source code we can navigate into two different directions using some hotkeys of our IDE. Either we dive deep into a concrete 
`Driving Adapter` such as `RESTfulRPCAdapter`. Or we follow direction `Inbound Port` &rarr; `Outbound Port` by selecting `TimeService` and enter 
the application core.  

### Enter the application core ###

If we select an inbound port such as `TimeService`, the constructor looks as follows. 

```java
public TimeService(ITimePublisher timePublisher, IMessageDisplay messageDisplay)
{
  // ...
}
```

Please remember the characteristics of an `Outbound Port`: 

*   It provides **required** methods to the application core.
*   It must be an interface because it can be implemented by different technology stacks.

In most object-oriented languages it is the job of the constructor to define all required attributes. Therefore, the constructor of an 
`Inbound Port` must define its required `Outbound Ports` and this represents `Inbound Port` &rarr; `Outbound Port`. Jexxa enforces this 
rule and requires that all parameters of the constructor must be `Outbound Ports`. These are the only parameters that are essential to 
create an `Inbound Port`. If you need any other parameters in the constructor of your `inbound port` it is most likely that you have an 
issue in your software design. 

Obviously, the constructor of an `inbound port` should only take these `outbound ports` that are required for its own use cases. Even in 
a large application, the constructor should take only a few parameters. Otherwise, you should think about splitting your `Inbound Port`.
Especially for a large application, you automatically hide a lot of source code and can focus on the source code for a specific use case.

At this point, we have following additional information:
*   Current `inbound port`: `TimeService`
*   Required `outbound ports`: `ITimePublisher` and `IMessageDisplay`

Again, we can navigate into two different directions. Either we dive deep into the application core by checking the implementation 
of `TimeService`. Please note that within a large application core you should use a so-called micro architecture that supports the
navigation through your application core. Please check tutorial [BookStore](https://github.com/repplix/Jexxa/tree/master/tutorials/BookStore)
to see a potential mapping of an onion architecture for your application core.   

Alternatively, you can select one of the two `Outbound Ports` from your IDE to continue in the direction of `Outbound Port` &rarr; 
`Driven Adapter`.
   
### Leave the application core ###

If we select `IMessageDisplay` we just see the following interface: 

```java
public interface IMessageDisplay
{                                       
  void show(String message);
}
```
This interface describes required methods from a domain's point of view that can only be implemented by using a technology stack such as a logger. 

The most important aspect here is the following: 

Our flow of control states the direction `Outbound Port` &rarr; `Driven Adapter`. But an `Outbound Port` is a much higher abstraction that must not 
depend on a specific infrastructure. So the direction of the dependency must be `Outbound Port` &larr; `Driven Adapter` which is done by using 
[dependency inversion principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle). For this purpose any object-oriented language uses 
the concept of an `interface`. That's the reason, why we must declare a high-level interface that belongs to our application core. 

This approach ensures that we can easily exchange the technology stack that is used by our application core. That's why the interface is so important
from an architectural point of view and represents one of the four building blocks of our macro-architecture.   

Finally, this interface is then implemented by a `Driven Adapter` which again belongs to the infrastructure. Your IDE typically provides hot-keys 
to switch to the concrete implementation of the interface which is located in the infrastructure part again. In this application the 
implementation is quite simple. 

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
                   
### One small exception ###

In reality there is always an exception that confirms the rule. Within Jexxa this exception is directly in the beginning `Driving Adapter` &rarr;
`Inbound Port`. The described rule works fine as long as the `Driving Adapter` can apply a convention how to automatically expose the methods 
of an `Inbound Port`. Unfortunately, this does not work if we have to change the representation style. For example this is required if we have to 
offer a RESTful API to our `Inbound Port`, or if we have to map asynchronous messages to `Inbound Ports`. 

In case we can not apply a convention, Jexxa represents the flow of control as follows:     

*   `Driving Adapter` &rarr; `Port Adapter` &rarr; `Inbound Port` &rarr; `Outbound Port` &rarr; `Driven Adapter`

You can see this in our tutorial as well: 

```java
void main(String[] args)
{   
    ///...
    jexxaMain

        ///...
        // Conditional bind is only executed if given expression evaluates to true
        .conditionalBind( TimeServiceApplication::isJMSEnabled, JMSAdapter.class).to(PublishTimeListener.class)
} 
```

Here, a JMS message should be handled by our application core. The `port adapter` belongs to the infrastructure and performs the specific mapping. 
Please note that this is a crucial aspect. In the past, I saw a lot of code where this mapping is directly done in the `Inbound Port`. From my 
understanding this is a massive architectural violation because we couple a technology stack directly to our application core. 

Anyway, from a navigation point of view we have to take just a single additional step to continue in the direction of `Port Adapter` &rarr; 
`Inbound Port`. To do so, just select `PublishTimeListener` in your IDE. Again, you have to check the constructor of `PublishTimeListener`. Within 
Jexxa this constructor must take exactly one parameter which must be an `Inbound Port`. In this case it is `TimeService` again as you can see in the
following code.          

```java
public PublishTimeListener(TimeService timeService)
{
    //...
}
```

By selecting the parameter of the constructor in our IDE, we can continue in the direction of `Inbound Port` &rarr; `Outbound Port`. Since it is no 
difference to follow the flow of control in the source code, Jexxa uses the same API to represent the starting point on both cases.

## Summary ##

I hope you got an idea how to easily navigate through your application using the underlying architecture.  
