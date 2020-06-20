package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.INTEGRATION_TEST)
class JDBCKeyValueRepositoryIT
{
    private JexxaAggregate aggregate;
    private JDBCKeyValueRepository<JexxaAggregate, JexxaValueObject> objectUnderTest;

    @BeforeEach
    void initTests() throws IOException
    {
        //Arrange
        aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.load(getClass().getResourceAsStream(JexxaMain.JEXXA_APPLICATION_PROPERTIES));

        objectUnderTest = new JDBCKeyValueRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties
        );
        objectUnderTest.removeAll();
    }

    @AfterEach
    void tearDown()
    {
        Optional.ofNullable(objectUnderTest)
                .ifPresent(JDBCKeyValueRepository::close);
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
    void getUnknownAggregate()
    {
        //arrange
        var unknownAggregate = JexxaAggregate.create(new JexxaValueObject(42));


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
}