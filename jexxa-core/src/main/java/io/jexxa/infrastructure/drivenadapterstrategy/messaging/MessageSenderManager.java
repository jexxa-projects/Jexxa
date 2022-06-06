package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging.MessageLogger;
import io.jexxa.utils.JexxaBanner;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.factory.ClassFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender.JNDI_FACTORY_KEY;
import static io.jexxa.utils.JexxaBanner.addBanner;

public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private static final Map<Class<?> , Class<? extends MessageSender>> STRATEGY_MAP = new HashMap<>();
    private static Class<? extends MessageSender> defaultStrategy = null;

    private MessageSenderManager()
    {
        addBanner(this::bannerInformation);
    }

    @CheckReturnValue
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

        // 3. If a JDBC driver is stated in Properties => Use JDBCKeyValueRepository
        if (properties.containsKey(JNDI_FACTORY_KEY))
        {
            return JMSSender.class;
        }

        // 4. If everything fails, return a IMDBRepository
        return MessageLogger.class;
    }

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
            if (result.isEmpty()) //Try default constructor
            {
                result = ClassFactory.newInstanceOf(strategy);
            }

            return result.orElseThrow();
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

    public void bannerInformation(Properties properties)
    {
        JexxaLogger.getLogger(JexxaBanner.class).info("Used Message Sender Strategie  : {}",getDefaultMessageSender(properties).getSimpleName());
    }
}
