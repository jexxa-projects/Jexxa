package io.jexxa.infrastructure;


import io.jexxa.common.JexxaBanner;
import io.jexxa.common.annotation.CheckReturnValue;
import io.jexxa.common.wrapper.factory.ClassFactory;
import io.jexxa.common.wrapper.jdbc.JexxaJDBCProperties;
import io.jexxa.infrastructure.persistence.repository.IRepository;
import io.jexxa.infrastructure.persistence.repository.imdb.IMDBRepository;
import io.jexxa.infrastructure.persistence.repository.jdbc.JDBCKeyValueRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static io.jexxa.common.JexxaJMSProperties.JEXXA_JMS_STRATEGY;
import static io.jexxa.common.wrapper.jdbc.JexxaJDBCProperties.JEXXA_REPOSITORY_STRATEGY;
import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;


public final class RepositoryManager
{
    private static final RepositoryManager REPOSITORY_MANAGER = new RepositoryManager();

    private static final Map<Class<?> , Class<?>> STRATEGY_MAP = new HashMap<>();
    private static Class<?> defaultStrategy = null;

    public static  <T,K> IRepository<T,K> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties)
    {
        return REPOSITORY_MANAGER.getStrategy(aggregateClazz, keyFunction, properties);
    }

    public static <U extends IRepository<?,?>, T > void setStrategy(Class<U> strategyType, Class<T> aggregateType)
    {
        STRATEGY_MAP.put(aggregateType, strategyType);
    }

    public static <U extends IRepository<?,?> > void setDefaultStrategy(Class<U> defaultStrategy)
    {
        RepositoryManager.defaultStrategy = defaultStrategy;
    }
    public static Class<?> getDefaultRepository(Properties properties)
    {
        return REPOSITORY_MANAGER.getStrategy(null, properties);
    }


    @SuppressWarnings("unchecked")
    @CheckReturnValue
    <T,K> IRepository<T,K> getStrategy(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties
    )
    {
        try
        {
            var strategy = getStrategy(aggregateClazz, properties);

            var result = ClassFactory.newInstanceOf(strategy, new Object[]{aggregateClazz, keyFunction, properties});

            return (IRepository<T, K>) result.orElseThrow();
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

    public static void defaultSettings( )
    {
        defaultStrategy = null;
        STRATEGY_MAP.clear();
    }


    private RepositoryManager()
    {
        JexxaBanner.addConfigBanner(this::bannerInformation);
    }

    @SuppressWarnings("DuplicatedCode")
    private <T> Class<?> getStrategy(Class<T> aggregateClazz, Properties properties)
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
        if (properties.containsKey(JEXXA_REPOSITORY_STRATEGY)) {
            try {
                return Class.forName(properties.getProperty(JEXXA_REPOSITORY_STRATEGY));
            } catch (ClassNotFoundException e) {
                getLogger(ObjectStoreManager.class).warn("Unknown or invalid repository {} -> Ignore setting", properties.getProperty(JEXXA_JMS_STRATEGY));
            }
        }
        // 4. If a JDBC driver is stated in Properties => Use JDBCKeyValueRepository
        if (properties.containsKey(JexxaJDBCProperties.JEXXA_JDBC_DRIVER))
        {
            return JDBCKeyValueRepository.class;
        }

        // 5. If everything fails, return a IMDBRepository
        return IMDBRepository.class;
    }

    public void bannerInformation(Properties properties)
    {
        getLogger(JexxaBanner.class).info("Used Repository Strategie      : [{}]",getDefaultRepository(properties).getSimpleName());
    }

}
