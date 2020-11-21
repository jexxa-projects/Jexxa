package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import static io.jexxa.TestConstants.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Properties;

import io.jexxa.application.domain.aggregate.JexxaEntity;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(INTEGRATION_TEST)
class RepositoryManagerIT
{

    @AfterEach
    void cleanup()
    {
        RepositoryManager.defaultSettings();
    }

    @Test
    void validateIMDBFallbackStrategy( )
    {
        //Arrange
        RepositoryManager.setDefaultStrategy(null);

        // Act
        var result =  RepositoryManager.getRepository(JexxaEntity.class,
                JexxaEntity::getKey,
                new Properties());

        //Assert
        assertNotNull(result);
        assertEquals(IMDBRepository.class.getName(), result.getClass().getName() );
    }

    @Test
    void validatePropertiesStrategy( )
    {
        //Arrange
        RepositoryManager.setDefaultStrategy(null);

        var postgresProperties = getDefaultProperties();

        //Act
        var result =  RepositoryManager.getRepository(JexxaEntity.class,
                JexxaEntity::getKey,
                postgresProperties);

        //Assert
        assertNotNull(result);
        assertEquals(JDBCKeyValueRepository.class.getName(), result.getClass().getName() );
    }


    @Test
    void validateDefaultStrategyOverProperties()
    {
        //Arrange: Define a JDBC connection in properties but also set a default strategy
        var postgresProperties = getDefaultProperties();

        //Act
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);

        var result =  RepositoryManager.getRepository(JexxaEntity.class,
                JexxaEntity::getKey,
                postgresProperties);

        //Assert
        assertNotNull(result);
        assertEquals(IMDBRepository.class.getName(), result.getClass().getName() );
    }

    @Test
    void validateSpecificStrategyOverDefaultStrategy()
    {
        //Arrange: Define a JDBC connection in properties but also set a default strategy
        var postgresProperties = getDefaultProperties();

        RepositoryManager.setDefaultStrategy(IMDBRepository.class);  // Set a default strategy which is used in case no specific strategy is defines

        //Act
        RepositoryManager.setStrategy(JDBCKeyValueRepository.class, JexxaEntity.class );  // Set a specific strategy

        var result =  RepositoryManager.getRepository(JexxaEntity.class,
                JexxaEntity::getKey,
                postgresProperties);

        //Assert
        assertNotNull(result);
        assertEquals(JDBCKeyValueRepository.class.getName(), result.getClass().getName() );
    }

    private Properties getDefaultProperties()
    {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCKeyValueRepository.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        return postgresProperties;
    }
}
