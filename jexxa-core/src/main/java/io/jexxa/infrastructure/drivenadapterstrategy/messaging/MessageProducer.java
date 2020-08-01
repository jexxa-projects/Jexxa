package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import io.jexxa.utils.CheckReturnValue;
import org.apache.commons.lang3.Validate;

@CheckReturnValue
public class MessageProducer
{
    enum DestinationType { TOPIC, QUEUE }
    private Properties properties;
    private final Object message;
    private final MessageSender jmsSender;

    private DestinationType destinationType;
    private String destination;

    <T> MessageProducer(T message, MessageSender jmsSender)
    {
        Validate.notNull(message);
        Validate.notNull(jmsSender);

        this.message = message;
        this.jmsSender = jmsSender;
    }

    @CheckReturnValue
    public MessageProducer toQueue(String destination)
    {
        this.destination = destination;
        this.destinationType = DestinationType.QUEUE;

        return this;
    }

    @CheckReturnValue
    public MessageProducer toTopic(String destination)
    {
        this.destination = destination;
        this.destinationType = DestinationType.TOPIC;

        return this;
    }

    @CheckReturnValue
    public MessageProducer addHeader(String key, String value)
    {
        if (properties == null)
        {
            properties = new Properties();
        }

        properties.put(key, value);

        return this;
    }
    
    public void asJson()
    {
        Gson gson = new Gson();

        as(gson::toJson);
    }

    public void asString()
    {
        as(message::toString);
    }


    public void as( Function<Object, String> serializer )
    {
        Validate.notNull(destination, "No destination in MessageProducer set");
        
        if (destinationType == DestinationType.QUEUE)
        {
            jmsSender.sendMessageToQueue(serializer.apply(message), destination, properties);
        }
        else
        {
            jmsSender.sendMessageToTopic(serializer.apply(message), destination, properties);
        }
    }

    public void as( Supplier<String> serializer )
    {
        Validate.notNull(destination,  "No destination in MessageProducer set");

        if (destinationType == DestinationType.QUEUE)
        {
            jmsSender.sendMessageToQueue(serializer.get(), destination, properties);
        }
        else
        {
            jmsSender.sendMessageToTopic(serializer.get(), destination, properties);
        }
    }

}
