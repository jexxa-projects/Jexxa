package io.jexxa.infrastructure.drivenadapterstrategy.persistence;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;


public class RepositoryManager
{
    private static final RepositoryManager REPOSITORY_MANAGER = new RepositoryManager();

    private final Map<Class<?> , Class<?>> strategyMap = new HashMap<>();
    private Class<?> defaultStrategy = null;


    public static RepositoryManager getInstance()
    {
        return REPOSITORY_MANAGER;
    }

    public <U extends IRepository<?,?>, T > void setStrategy(Class<U> strategyType, Class<T> aggregateType)
    {
        strategyMap.put(aggregateType, strategyType);
    }

    public <U extends IRepository<?,?> > void setDefaultStrategy(Class<U> defaultStrategy)
    {
        this.defaultStrategy = defaultStrategy;
    }

    @SuppressWarnings("unchecked")
    public <T,K> IRepository<T,K> getStrategy(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties
    )
    {
        try {
            var constructor = getStrategy(aggregateClazz, properties)
                    .getConstructor(Class.class, Function.class, Properties.class);

            return (IRepository<T,K>)constructor.newInstance(aggregateClazz, keyFunction, properties);
        }
        catch (ReflectiveOperationException e)
        {
            if ( e.getCause() != null)
            {
                throw new IllegalArgumentException(e.getCause().getMessage(), e.getCause());
            }

            throw new IllegalArgumentException("No suitable default IRepository available", e);
        }
    }


    /**
     * @deprecated Use {@link #getStrategy(Class, Function, Properties)} instead 
     */
    @Deprecated(forRemoval=true)
    public static <T,K> IRepository<T,K> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties
    )
    {
        return getInstance().getStrategy(aggregateClazz, keyFunction, properties);
    }

    private RepositoryManager()
    {
        //Package protected constructor
    }

    private <T> Class<?> getStrategy(Class<T> aggregateClazz, Properties properties)
    {
        // 1. Check if a dedicated strategy is registered for aggregateClazz
        var result = strategyMap
                .entrySet()
                .stream()
                .filter( element -> element.getKey().equals(aggregateClazz)).findFirst();

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
            return JDBCKeyValueRepository.class;
        }

        // 4. If everything fails, return a IMDBRepository
        return IMDBRepository.class;
    }

}
