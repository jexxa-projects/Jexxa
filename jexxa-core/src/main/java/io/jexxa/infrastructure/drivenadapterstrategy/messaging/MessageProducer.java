package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import io.jexxa.utils.annotations.CheckReturnValue;
import org.apache.commons.lang3.Validate;

@CheckReturnValue
public class MessageProducer
{
    public enum DestinationType { TOPIC, QUEUE }
    private Properties properties;
    private final Object message;
    private final MessageSender messageSender;

    private DestinationType destinationType;
    private String destination;

    protected <T> MessageProducer(T message, MessageSender messageSender)
    {
        Validate.notNull(message);
        Validate.notNull(messageSender);

        this.message = message;
        this.messageSender = messageSender;
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
            messageSender.sendToQueue( serializer.apply(message), destination, properties);
        }
        else
        {
            messageSender.sendToTopic( serializer.apply(message), destination, properties);
        }
    }

    private void as( Supplier<String> serializer )
    {
        Validate.notNull(destination,  "No destination in MessageProducer set");

        if (destinationType == DestinationType.QUEUE)
        {
            messageSender.sendToQueue(serializer.get(), destination, properties);
        }
        else
        {
            messageSender.sendToTopic(serializer.get(), destination, properties);
        }
    }


}
