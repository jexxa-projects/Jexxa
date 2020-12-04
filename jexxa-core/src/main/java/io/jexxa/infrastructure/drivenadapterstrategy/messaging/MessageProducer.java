package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import static java.util.UUID.randomUUID;

import java.time.Instant;
import java.util.Properties;
import java.util.function.Consumer;
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

    private Object messageContainer;
    private Consumer<String> payloadConsumer;

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
        if ( messageContainer != null && payloadConsumer != null)
        {
            payloadConsumer.accept(message.toString());
            as(messageContainer::toString);
        } else {
            as(message::toString);
        }
    }


    public void asDomainEvent()
    {
        var container = new UnpublishedDomainEventContainer(
                randomUUID().toString(),
                message.getClass().getName(),
                Instant.now());

        inContainer( container, container::setPayload )
                .asJson();
    }

    public <U> MessageProducer inContainer(U container, Consumer<String> payloadConsumer)
    {
        this.messageContainer = container;
        this.payloadConsumer = payloadConsumer;
        return this;
    }



    public void as( Function<Object, String> serializer )
    {
        Validate.notNull(destination, "No destination in MessageProducer set");

        if (destinationType == DestinationType.QUEUE)
        {
            messageSender.sendToQueue(serializeMessage(serializer), destination, properties);
        }
        else
        {
            messageSender.sendToTopic(serializeMessage(serializer), destination, properties);
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

    private String serializeMessage( Function<Object, String> serializer )
    {
        var serializedMessage = serializer.apply(message);
        if ( messageContainer != null && payloadConsumer != null )
        {
            payloadConsumer.accept(serializedMessage);

            return serializer.apply(messageContainer);
        }

        return serializedMessage;
    }

    @SuppressWarnings({"unused", "java:S1068", "java:S1450", "FieldCanBeLocal"}) // for attributes. We need them for proper json serialization
    static class UnpublishedDomainEventContainer
    {
        private final String uuid;
        private final String payloadType;
        private String payload;
        private final Instant publishedAt;

        UnpublishedDomainEventContainer(String uuid, String payloadType, Instant publishedAt )
        {
            this.uuid = uuid;
            this.payloadType = payloadType;
            this.publishedAt = publishedAt;
        }

        public void setPayload(String payload)
        {
            this.payload = payload;
        }
    }
}
