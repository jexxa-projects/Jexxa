package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryConfig;

import java.util.Properties;
import java.util.stream.Stream;

public final class ObjectStoreTestDatabase
{
    public static final String REPOSITORY_CONFIG = "io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase#repositoryConfig";

    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfig() {
        return RepositoryConfig.repositoryConfig("objectstore");
    }

    private ObjectStoreTestDatabase()
    {
        //private constructor
    }
}
