package io.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import static io.jexxa.TestTags.INTEGRATION_TEST;

import java.io.IOException;
import java.util.Properties;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(INTEGRATION_TEST)
public class JDBCConnectionIT
{
    private JexxaAggregate aggregate;
    private JDBCConnection<JexxaAggregate, JexxaValueObject> objectUnderTest;

    @BeforeEach
    public void initTests() throws IOException
    {
        //Arrange
        aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.load(getClass().getResourceAsStream(JexxaMain.JEXXA_APPLICATION_PROPERTIES));

        objectUnderTest = new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties
        );
        objectUnderTest.removeAll();
    }

    @AfterEach
    public void teardown()
    {
        if ( objectUnderTest != null )
        {
            objectUnderTest.close();
        }
    }


    @Test
    public void addAggregate()
    {
        //act
        objectUnderTest.add(aggregate);

        //Assert
        Assertions.assertEquals(aggregate.getKey(), objectUnderTest.get(aggregate.getKey()).orElseThrow().getKey());
        Assertions.assertTrue(objectUnderTest.get().size() > 0);
    }


    @Test
    public void removeAggregate()
    {
        //Arrange
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assertions.assertTrue(objectUnderTest.get().isEmpty());
    }

    @Test
    public void testExceptionInvalidOperations()
    {
        //Exception if key is used to add twice
        objectUnderTest.add(aggregate);
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.add(aggregate));

        //Exception if unknown key is removed
        objectUnderTest.remove(aggregate.getKey());
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.remove(aggregate.getKey()));

        //Exception if unknown aggregate ist updated
        Assertions.assertThrows(IllegalArgumentException.class, () ->objectUnderTest.update(aggregate));
    }
}