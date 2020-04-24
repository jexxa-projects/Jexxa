package io.ddd.jexxa.infrastructure.drivenadapter.persistence;


import java.util.Properties;
import java.util.function.Function;

import io.ddd.jexxa.infrastructure.drivenadapter.persistence.imdb.IMDBConnection;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCConnection;
import io.ddd.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;


public class RepositoryManager
{
    private static final Logger LOGGER = JexxaLogger.getLogger(RepositoryManager.class);
    private static final RepositoryManager REPOSITORY_MANAGER = new RepositoryManager();


    @SuppressWarnings("unchecked")
    public static <T,K> IRepositoryConnection<T,K> getConnection(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            Properties properties
    )
    {
        try {
            var constructor = REPOSITORY_MANAGER
                    .getDefaultConnection(properties)
                    .getConstructor(Class.class, Function.class, Properties.class);
            
            return (IRepositoryConnection<T,K>)constructor.newInstance(aggregateClazz, keyFunction, properties);
        }
        catch (ReflectiveOperationException e)
        {
            LOGGER.error("No suitable default IRepositoryConnection available.");
            throw new IllegalStateException("No suitable default IRepositoryConnection available", e);
        }
    }

    private RepositoryManager()
    {

    }

    private Class<?> getDefaultConnection(Properties properties)
    {
        if (properties.containsKey(JDBCConnection.JDBC_DRIVER))
        {
            return JDBCConnection.class;
        }
        return IMDBConnection.class;
    }

}
