package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import org.apache.commons.lang3.Validate;

public class JMSMessage
{
    private Properties properties;
    private final Object message;
    private final JMSSender jmsSender;
    private JMSQueue queueDestination;
    private JMSTopic topicDestination;

    <T> JMSMessage(T message, JMSSender jmsSender)
    {
        Validate.notNull(message);
        Validate.notNull(jmsSender);
        
        this.message = message;
        this.jmsSender = jmsSender;
    }

    public JMSMessage to(JMSQueue queue)
    {
        this.queueDestination = queue;
        return this;
    }

    public JMSMessage to(JMSTopic topic)
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
        if (queueDestination != null)
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

        if (queueDestination != null)
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
        
        if (queueDestination != null)
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

        if (queueDestination != null)
        {
            jmsSender.sendTextToQueue(message.toString(), queueDestination.getDestination(), properties);
        }
        else
        {
            jmsSender.sendTextToTopic(message.toString(), topicDestination.getDestination(), properties);
        }
    }
}
