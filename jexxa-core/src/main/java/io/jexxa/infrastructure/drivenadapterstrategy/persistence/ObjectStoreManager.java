package io.jexxa.infrastructure.drivenadapterstrategy.persistence;


import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection.JDBC_DRIVER;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.MetadataComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCObjectStore;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.factory.ClassFactory;


@SuppressWarnings("unused")
public final class ObjectStoreManager
{
    private static final ObjectStoreManager REPOSITORY_MANAGER = new ObjectStoreManager();

    private static final Map<Class<?> , Class<?>> strategyMap = new HashMap<>();
    private static Class<?> defaultStrategy = null;


    public static  <T,K,M  extends Enum<?> & MetadataComparator> IObjectStore<T,K, M> getObjectStore(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Class<M> comparatorFunctions,
            Properties properties)
    {
        return REPOSITORY_MANAGER.getStrategy(aggregateClazz, keyFunction, comparatorFunctions, properties);
    }

    public static <U extends IObjectStore<?,?,?>, T > void setStrategy(Class<U> strategyType, Class<T> aggregateType)
    {
        strategyMap.put(aggregateType, strategyType);
    }

    public static <U extends IObjectStore<?,?,?>> void setDefaultStrategy(Class<U> defaultStrategy)
    {
        ObjectStoreManager.defaultStrategy = defaultStrategy;
    }

    @SuppressWarnings("unchecked")
    @CheckReturnValue
    public <T,K,M  extends Enum<?> & MetadataComparator> IObjectStore<T,K,M> getStrategy(
            Class<T> objectClazz,
            Function<T,K> keyFunction,
            Class<M> comparatorFunctions,
            Properties properties
    )
    {

        try
        {
            var strategy = getStrategy(objectClazz, properties);

            var result = ClassFactory.newInstanceOf(strategy, new Object[]{objectClazz, keyFunction, comparatorFunctions, properties});

            return (IObjectStore<T, K,M>) result.orElseThrow();
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


    private ObjectStoreManager()
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
        if (properties.containsKey(JDBC_DRIVER))
        {
            return JDBCObjectStore.class;
        }

        // 4. If everything fails, return a IMDBRepository
        return IMDBObjectStore.class;
    }

}
