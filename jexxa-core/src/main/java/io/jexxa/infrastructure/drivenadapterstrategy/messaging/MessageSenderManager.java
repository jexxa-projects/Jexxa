package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import jdk.jfr.Experimental;

@Experimental
public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private Class<?> defaultStrategy = JMSSender.class;

    private MessageSenderManager()
    {
        //Private constructor
    }

    public MessageSender getStrategy(Properties properties)
    {
        try {
            var constructor = defaultStrategy.getConstructor(Properties.class);

            return (MessageSender)constructor.newInstance(properties);
        }
        catch (ReflectiveOperationException e)
        {
            if ( e.getCause() != null)
            {
                throw new IllegalArgumentException(e.getCause().getMessage(), e);
            }

            throw new IllegalArgumentException("No suitable default IRepository available", e);
        }
    }

    public void setDefaultStrategy(Class<?> defaultStrategy)
    {
        this.defaultStrategy = defaultStrategy;
    }

    public static MessageSenderManager getInstance()
    {
        return MESSAGE_SENDER_MANAGER;
    }
}
