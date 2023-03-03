package io.jexxa.drivenadapter.strategy.messaging;

import io.jexxa.drivenadapter.strategy.messaging.logging.MessageLogger;
import io.jexxa.drivenadapter.strategy.outbox.TransactionalOutboxSender;
import io.jexxa.utils.JexxaBanner;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.api.annotation.CheckReturnValue;
import io.jexxa.api.wrapper.factory.ClassFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.utils.JexxaBanner.addConfigBanner;
import static io.jexxa.utils.properties.JexxaJMSProperties.*;

public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private static final Map<Class<?> , Class<? extends MessageSender>> STRATEGY_MAP = new HashMap<>();
    private static Class<? extends MessageSender> defaultStrategy = null;

    private MessageSenderManager()
    {
        addConfigBanner(this::bannerInformation);
    }

    @CheckReturnValue
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    // When registering a MessageSender we check its type so that we can perform an unchecked cast
    private <T> Class<? extends MessageSender> getStrategy(Class<T> aggregateClazz, Properties properties)
    {
        // 1. Check if a dedicated strategy is registered for aggregateClazz
        var result = STRATEGY_MAP
                .entrySet()
                .stream()
                .filter( element -> element.getKey().equals(aggregateClazz))
                .filter( element -> element.getValue() != null )
                .findFirst();

        if (result.isPresent())
        {
            return result.get().getValue();
        }

        // 2. If a default strategy is available, return this one
        if (defaultStrategy != null)
        {
            return defaultStrategy;
        }

        // 3. Check explicit configuration
        if (properties.containsKey(JEXXA_JMS_STRATEGY))
        {
            try {
                return (Class<? extends MessageSender>) Class.forName(properties.getProperty(JEXXA_JMS_STRATEGY));
            } catch (ClassNotFoundException e)
            {
                JexxaLogger.getLogger(MessageSenderManager.class).warn("Unknown or invalid message sender {} -> Ignore setting", properties.getProperty(JEXXA_JMS_STRATEGY));
                JexxaLogger.getLogger(MessageSenderManager.class).warn(String.valueOf(e));
            }
        }

        // 4. If a JNDI Factory is defined and simulation mode is deactivated => Use JMSSender
        if (properties.containsKey(JNDI_FACTORY_KEY) && !properties.containsKey(JEXXA_JMS_SIMULATE))
        {
            return TransactionalOutboxSender.class;
        }

        // 5. In all other cases (including simulation mode) return a MessageLogger
        return MessageLogger.class;
    }

    @SuppressWarnings("unused")
    public static Class<?> getDefaultMessageSender(Properties properties)
    {
        return getMessageSender(null, properties).getClass();
    }

    @SuppressWarnings("unused")
    public static <U extends MessageSender, T > void setStrategy(Class<U> strategyType, Class<T> aggregateType)
    {
        STRATEGY_MAP.put(aggregateType, strategyType);
    }

    public static void setDefaultStrategy(Class<? extends MessageSender>  defaultStrategy)
    {
        Objects.requireNonNull(defaultStrategy);

        MessageSenderManager.defaultStrategy = defaultStrategy;
    }


    public static <T>  MessageSender getMessageSender(Class<T> sendingClass, Properties properties)
    {
        try
        {
            var strategy = MESSAGE_SENDER_MANAGER.getStrategy(sendingClass, properties);

            var result = ClassFactory.newInstanceOf(strategy, new Object[]{properties});
            if (result.isEmpty()) //Try factory method with properties
            {
                result = ClassFactory.newInstanceOf(MessageSender.class, strategy,new Object[]{properties});
            }
            if (result.isEmpty()) //Try default constructor
            {
                result = ClassFactory.newInstanceOf(strategy);
            }
            if (result.isEmpty()) //Try factory method without properties
            {
                result = ClassFactory.newInstanceOf(MessageSender.class, strategy);
            }

            return result.orElseThrow();
        }
        catch (ReflectiveOperationException e)
        {
            if ( e.getCause() != null)
            {
                throw new IllegalArgumentException(e.getCause().getMessage(), e);
            }

            throw new IllegalArgumentException("No suitable default MessageSender available", e);
        }
    }

    public void bannerInformation(Properties properties)
    {
        JexxaLogger.getLogger(JexxaBanner.class).info("Used Message Sender Strategie  : [{}]", MESSAGE_SENDER_MANAGER.getStrategy(null, properties).getSimpleName());
    }
}
