package io.jexxa.common.wrapper.jdbc;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.model.JexxaEntity;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.infrastructure.persistence.repository.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.util.Properties;

import static io.jexxa.common.JexxaCoreProperties.JEXXA_APPLICATION_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JDBCKeyValueRepositoryIT
{
    private JexxaEntity aggregate;
    private JDBCKeyValueRepository<JexxaEntity, JexxaValueObject> objectUnderTest;

    @BeforeEach
    void initTests() throws IOException
    {
        //Arrange
        aggregate = JexxaEntity.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.load(getClass().getResourceAsStream(JEXXA_APPLICATION_PROPERTIES));

        try (JDBCConnection connection = new JDBCConnection(properties) )
        {
            connection.createTableCommand(JDBCKeyValueRepository.KeyValueSchema.class)
                    .dropTableIfExists(JexxaEntity.class)
                    .asIgnore();
        }

        objectUnderTest = new JDBCKeyValueRepository<>(
                JexxaEntity.class,
                JexxaEntity::getKey,
                properties
        );
    }


    @Test
    void addAggregate()
    {
        //act
        objectUnderTest.add(aggregate);

        //Assert
        assertEquals(aggregate.getKey(), objectUnderTest.get(aggregate.getKey()).orElseThrow().getKey());
        assertEquals(1, objectUnderTest.get().size());
    }

    @Test
    void getUnavailableAggregate()
    {
        //arrange
        var unknownAggregate = JexxaEntity.create(new JexxaValueObject(42));


        //act
        var result = objectUnderTest.get(unknownAggregate.getKey());

        //Assert
        assertTrue(result.isEmpty());
    }


    @Test
    void removeAggregate()
    {
        //Arrange
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        assertTrue(objectUnderTest.get().isEmpty());
    }

    @Test
    void testExceptionInvalidOperations()
    {
        //Exception if key is used to add twice
        objectUnderTest.add(aggregate);
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.add(aggregate));

        //Exception if unknown key is removed
        var key = aggregate.getKey();
        objectUnderTest.remove(key);
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.remove(key));

        //Exception if unknown aggregate ist updated
        assertThrows(IllegalArgumentException.class, () ->objectUnderTest.update(aggregate));
    }

    @Test
    void testReconnect()
    {
        objectUnderTest.getConnection().close();
        assertDoesNotThrow(this::getUnavailableAggregate);

        objectUnderTest.getConnection().close();
        assertDoesNotThrow(this::removeAggregate);

        objectUnderTest.getConnection().close();
        assertDoesNotThrow(this::testExceptionInvalidOperations);

        objectUnderTest.getConnection().close();
        assertDoesNotThrow(this::addAggregate);
    }

    @Test
    void testSuccessfulTransaction()
    {
        //Arrange
        objectUnderTest.initTransaction();
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.closeTransaction();


        //Assert
        assertFalse(objectUnderTest.get(aggregate.getKey()).isEmpty());
    }

    @Test
    void testTransactionRollback()
    {
        //Arrange
        objectUnderTest.initTransaction();
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.rollback();
        objectUnderTest.closeTransaction();


        //Assert
        assertTrue(objectUnderTest.get(aggregate.getKey()).isEmpty());
    }

    @Test
    void testFailedTransaction()
    {
        //Arrange
        objectUnderTest.initTransaction();
        objectUnderTest.add(aggregate);

        //act
        try {
            objectUnderTest.add(aggregate);
        } catch (IllegalArgumentException e) {
            objectUnderTest.rollback();
        }
        objectUnderTest.closeTransaction();


        //Assert - since the second .add statement failed, no aggregate has been added
        assertTrue(objectUnderTest.get(aggregate.getKey()).isEmpty());
    }

}