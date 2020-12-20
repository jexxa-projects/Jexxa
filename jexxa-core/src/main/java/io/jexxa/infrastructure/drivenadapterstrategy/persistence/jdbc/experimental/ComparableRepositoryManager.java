package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.factory.ClassFactory;


public final class ComparableRepositoryManager
{
    private static final ComparableRepositoryManager REPOSITORY_MANAGER = new ComparableRepositoryManager();

    private static final Map<Class<?> , Class<?>> strategyMap = new HashMap<>();
    private static Class<?> defaultStrategy = null;



    public static  <T,K,M  extends Enum<?> & Strategy> IComparableRepository<T,K, M> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Set<M> comparatorFunctions,
            Properties properties)
    {
        return REPOSITORY_MANAGER.getStrategy(aggregateClazz, keyFunction, comparatorFunctions, properties);
    }

    public static <U extends IComparableRepository<?,?,?>, T > void setStrategy(Class<U> strategyType, Class<T> aggregateType)
    {
        strategyMap.put(aggregateType, strategyType);
    }

    public static <U extends IComparableRepository<?,?,?>> void setDefaultStrategy(Class<U> defaultStrategy)
    {
        ComparableRepositoryManager.defaultStrategy = defaultStrategy;
    }

    @SuppressWarnings("unchecked")
    @CheckReturnValue
    public <T,K,M  extends Enum<?> & Strategy> IComparableRepository<T,K,M> getStrategy(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Set<M> comparatorFunctions,
            Properties properties
    )
    {
        try
        {
            var strategy = getStrategy(aggregateClazz, properties);

            var result = ClassFactory.newInstanceOf(strategy, new Object[]{aggregateClazz, keyFunction, comparatorFunctions, properties});

            return (IComparableRepository<T, K,M>) result.orElseThrow();
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
        strategyMap.clear();
    }


    private ComparableRepositoryManager()
    {
        //Package protected constructor
    }

    private <T> Class<?> getStrategy(Class<T> aggregateClazz, Properties properties)
    {
        // 1. Check if a dedicated strategy is registered for aggregateClazz
        var result = strategyMap
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
        if (properties.containsKey(JDBCKeyValueRepository.JDBC_DRIVER))
        {
            return JDBCComparableRepository.class;
        }

        // 4. If everything fails, return a IMDBRepository
        return IMDBComparableRepository.class;
    }

}
