package io.jexxa.infrastructure.persistence.objectstore;

import io.jexxa.infrastructure.persistence.RepositoryConfig;

import java.util.Properties;
import java.util.stream.Stream;

public final class ObjectStoreTestDatabase
{
    public static final String REPOSITORY_CONFIG = "io.jexxa.infrastructure.persistence.objectstore.ObjectStoreTestDatabase#repositoryConfig";

    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfig() {
        return RepositoryConfig.repositoryConfig("objectstore");
    }

    private ObjectStoreTestDatabase()
    {
        //private constructor
    }
}
