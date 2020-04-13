package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.function.Function;

import io.ddd.jexxa.infrastructure.drivenadapter.persistence.inmemory.InMemoryRepository;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCConnection;

public class RepositoryManager
{
    public static <T,K> IRepositoryConnection<T,K> getConnection(Class<T> aggregateClazz, Class<K> keyClazz, Function<T,K> keyFunction, Properties properties)
    {
        //return new JDBCConnection<>(aggregateClazz, keyClazz, keyFunction, properties);
        return new InMemoryRepository<>(aggregateClazz, keyClazz, keyFunction, properties);
    }

    private RepositoryManager()
    {

    }
}
