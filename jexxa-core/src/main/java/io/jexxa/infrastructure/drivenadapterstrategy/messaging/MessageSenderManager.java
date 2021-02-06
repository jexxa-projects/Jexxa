package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.factory.ClassFactory;

public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private static Class<? extends MessageSender> defaultStrategy = JMSSender.class;

    private MessageSenderManager()
    {
        //Private constructor
    }

    @CheckReturnValue
    MessageSender getStrategy(Properties properties)
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

    public static void setDefaultStrategy(Class<? extends MessageSender>  defaultStrategy)
    {
        Objects.requireNonNull(defaultStrategy);

        MessageSenderManager.defaultStrategy = defaultStrategy;
    }

    public static Class<? extends MessageSender> getDefaultStrategy()
    {
        return defaultStrategy;
    }


    public static MessageSender getMessageSender(Properties properties) { return MESSAGE_SENDER_MANAGER.getStrategy(properties); }
}
