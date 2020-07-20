package io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms;

import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.Message;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.Queue;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.Topic;
import org.apache.commons.lang3.Validate;

public class JMSMessage implements Message
{
    private Properties properties;
    private final Object message;
    private final JMSSender jmsSender;
    private Queue queueDestination;
    private Topic topicDestination;

    <T> JMSMessage(T message, JMSSender jmsSender)
    {
        Validate.notNull(message);
        Validate.notNull(jmsSender);
        
        this.message = message;
        this.jmsSender = jmsSender;
    }

    public JMSMessage to(Queue queue)
    {
        this.queueDestination = queue;
        return this;
    }

    public JMSMessage to(Topic topic)
    {
        this.topicDestination = topic;
        return this;
    }

    public JMSMessage addHeader(String key, String value)
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
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set  

        Gson gson = new Gson();
        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendTextToQueue(gson.toJson(message), queueDestination.getDestination(), properties);
        }
        else
        {
            jmsSender.sendTextToTopic(gson.toJson(message), topicDestination.getDestination(), properties);
        }
    }

    //Experimental
    public void as( Function<Object, String> serializer )
    {
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set

        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendTextToQueue(serializer.apply(message), queueDestination.getDestination(), properties);
        }
        else
        {
            jmsSender.sendTextToTopic(serializer.apply(message), topicDestination.getDestination(), properties);
        }
    }

    //Experimental
    public void as( Supplier<String> serializer )
    {
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set
        
        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendTextToQueue(serializer.get(), queueDestination.getDestination(), properties);
        }
        else
        {
            jmsSender.sendTextToTopic(serializer.get(), topicDestination.getDestination(), properties);
        }
    } 

    public void asString()
    {
        Validate.isTrue((queueDestination == null) ^ (topicDestination== null)); // exact one destination is set

        if (Objects.nonNull(queueDestination))
        {
            jmsSender.sendTextToQueue(message.toString(), queueDestination.getDestination(), properties);
        }
        else
        {
            jmsSender.sendTextToTopic(message.toString(), topicDestination.getDestination(), properties);
        }
    }
}
