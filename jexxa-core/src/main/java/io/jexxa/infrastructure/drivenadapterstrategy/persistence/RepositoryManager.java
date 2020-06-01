package io.jexxa.infrastructure.drivenadapterstrategy.persistence;


import java.util.Properties;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;


public class RepositoryManager
{
    private static final RepositoryManager REPOSITORY_MANAGER = new RepositoryManager();


    @SuppressWarnings("unchecked")
    public static <T,K> IRepository<T,K> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties
    )
    {
        try {
            var constructor = REPOSITORY_MANAGER
                    .getDefaultConnection(properties)
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

    private RepositoryManager()
    {
        //Package protected constructor
    }

    private Class<?> getDefaultConnection(Properties properties)
    {
        if (properties.containsKey(JDBCKeyValueRepository.JDBC_DRIVER))
        {
            return JDBCKeyValueRepository.class;
        }
        return IMDBRepository.class;
    }

}
