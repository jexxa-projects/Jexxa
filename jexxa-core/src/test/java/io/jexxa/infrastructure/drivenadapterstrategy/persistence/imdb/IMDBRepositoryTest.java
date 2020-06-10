package io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.UNIT_TEST)
class IMDBRepositoryTest
{

    @Test
    void addAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();

        //act
        objectUnderTest.add(aggregate);

        //Assert
        assertEquals(aggregate, objectUnderTest.get(aggregate.getKey()).orElse(null));
        assertTrue(objectUnderTest.get().size() > 0);
    }

    @Test
    void addAggregateTwice()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        
        //act
        objectUnderTest.add(aggregate);
        assertThrows(IllegalArgumentException.class, () ->objectUnderTest.add(aggregate));
    }


    @Test
    void removeAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        assertTrue(objectUnderTest.get().isEmpty());
    }

    @Test
    void differentConnections()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        var newConnection = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );

        //Assert that connections are different but refer to the same repository 
        assertNotEquals(objectUnderTest, newConnection);
        assertFalse(objectUnderTest.get().isEmpty());
        assertFalse(newConnection.get().isEmpty());
    }

    @Test
    void differentRepositories()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        var newConnection = new IMDBRepository<>(
                JexxaValueObject.class,
                JexxaValueObject::getValue,
                new Properties()
        );
        newConnection.removeAll();
        newConnection.add(new JexxaValueObject(42));

        //Assert that connections are different but refer to the same repository
        assertNotEquals(objectUnderTest, newConnection);
        assertEquals(1, objectUnderTest.get().size());
        assertEquals(1, newConnection.get().size());

    }


}
