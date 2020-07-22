package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.core.factory.ClassFactory;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import jdk.jfr.Experimental;

@Experimental
public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private Class<? extends MessageSender> defaultStrategy = JMSSender.class;

    private MessageSenderManager()
    {
        //Private constructor
    }

    public MessageSender getStrategy(Properties properties)
    {
        try {
            Optional<MessageSender> strategy = ClassFactory.newInstanceOf(defaultStrategy, Stream.of(properties).toArray());

            if ( strategy.isEmpty() )
            {
                strategy = ClassFactory.newInstanceOf(defaultStrategy);
            }

            if (strategy.isEmpty())
            {
                throw new IllegalArgumentException("No suitable constructor for " + defaultStrategy.getName());
            }

            return strategy.get();
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

    public void setDefaultStrategy(Class<? extends MessageSender>  defaultStrategy)
    {
        this.defaultStrategy = defaultStrategy;
    }

    public static MessageSenderManager getInstance()
    {
        return MESSAGE_SENDER_MANAGER;
    }
}
