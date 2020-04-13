package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.function.Function;

import io.ddd.jexxa.infrastructure.drivenadapter.persistence.inmemory.InMemoryRepository;

public class RepositoryManager
{
    public static <T,K> IRepositoryConnection<T,K> getConnection(Function<T,K> keyFunction, Properties properties)
    {
        return new InMemoryRepository<>(keyFunction);
    }

    private RepositoryManager()
    {

    }
}
