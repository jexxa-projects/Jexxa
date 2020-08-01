package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Optional;
import java.util.Properties;

import io.jexxa.core.factory.ClassFactory;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.utils.CheckReturnValue;
import org.apache.commons.lang3.Validate;

public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private Class<? extends MessageSender> defaultStrategy = JMSSender.class;

    private MessageSenderManager()
    {
        //Private constructor
    }

    @CheckReturnValue
    public MessageSender getStrategy(Properties properties)
    {
        try {
            Optional<MessageSender> strategy = ClassFactory.newInstanceOf(defaultStrategy, new Object[]{properties});

            if (strategy.isPresent())
            {
                return strategy.get();
            }

            return ClassFactory.newInstanceOf(defaultStrategy).orElseThrow();
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
        Validate.notNull(defaultStrategy);
        
        this.defaultStrategy = defaultStrategy;
    }

    public static MessageSenderManager getInstance()
    {
        return MESSAGE_SENDER_MANAGER;
    }
}
