package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import jdk.jfr.Experimental;
import org.apache.commons.lang3.Validate;

@Experimental
public class MessageProducer
{
    private Properties properties;
    private final Object message;
    private final MessageSender jmsSender;
    private Queue queueDestination;
    private Topic topicDestination;

    <T> MessageProducer(T message, MessageSender jmsSender)
    {
        Validate.notNull(message);
        Validate.notNull(jmsSender);

        this.message = message;
        this.jmsSender = jmsSender;
    }

    public MessageProducer to(Queue queue)
    {
        Validate.notNull(queue);
        Validate.isTrue(Objects.isNull(topicDestination)); // exact one destination is set

        this.queueDestination = queue;
        return this;
    }

    public MessageProducer to(Topic topic)
    {
        Validate.notNull(topic);
        Validate.isTrue(Objects.isNull(queueDestination)); // exact one destination is set

        this.topicDestination = topic;
        return this;
    }

    public MessageProducer addHeader(String key, String value)
    {
        if (properties == null)
        {
            properties = new Properties();
        }

        properties.put(key, value);

        return this;
    }

    public MessageProducer withHeader(String key, String value)
    {
        return addHeader(key, value);
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


    //Experimental
    public void as( Function<Object, String> serializer )
    {
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set

        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendMessage(serializer.apply(message), queueDestination, properties);
        }
        else
        {
            jmsSender.sendMessage(serializer.apply(message), topicDestination, properties);
        }
    }

    public void as( Supplier<String> serializer )
    {
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set

        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendMessage(serializer.get(), queueDestination, properties);
        }
        else
        {
            jmsSender.sendMessage(serializer.get(), topicDestination, properties);
        }
    }

    public static Topic topicOf(String destination)
    {
        return new Topic(destination);
    }

    public static Queue queueOf(String destination)
    {
        return new Queue(destination);
    }

    public static class Queue
    {
        private final String destination;

        private Queue(String destination)
        {
            this.destination = destination;
        }

        public String getDestination()
        {
            return destination;
        }
    }

    public static class Topic
    {
        private final String destination;

        private Topic(String destination)
        {
            this.destination = destination;
        }

        public String getDestination()
        {
            return destination;
        }
    }
}
