
*   Show the structure of an onion architecture by packages => will be used in all future examples 

*   Show writing an inbound port: Only outbound ports are valid parameters 

*   Show how to implement a driven adapter

## Dependencies

Apart from Jexxa we also need an JMS client. In this example we use ActiveMQ. Therefore, we need to add following dependencies

```maven
    <dependency>
      <groupId>org.apache.activemq</groupId>
      <artifactId>activemq-client</artifactId>
      <version></version>
    </dependency>
 ```       

#Properties 

```
#Settings for RESTfulRPCAdapter
io.jexxa.rest.host=localhost
io.jexxa.rest.port=7000

#Settings for JMSAdapter and JMSSender
java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory
java.naming.provider.url=tcp://localhost:61616
java.naming.user=admin
java.naming.password=admin

```