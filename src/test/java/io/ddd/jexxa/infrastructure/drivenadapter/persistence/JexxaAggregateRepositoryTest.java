package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Arrays;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.ddd.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepository;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class JexxaAggregateRepositoryTest
{
    @Parameterized.Parameter
    public Properties repositoryProperties;

    @Parameterized.Parameters
    public static Iterable<Properties> data() {
        var properties = new Properties();
        properties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        properties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        properties.put(JDBCConnection.JDBC_AUTOCREATE, "true");
        properties.put(JDBCConnection.JDBC_DEFAULT_URL, "jdbc:postgresql://localhost:5432/postgres");

        return Arrays.asList(new Properties(), properties);
    }


    private IJexxaAggregateRepository objectUnderTest;

    @Before
    public void initTests()
    {
        objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();
    }

    @Test
    public void addAggregate()
    {
        //Arrange
        var counterStream = Stream.of(100);
        var aggregateList = counterStream
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .collect( Collectors.toList() );

        //Act
        aggregateList.forEach(objectUnderTest::add);

        //Assert
        Assert.assertEquals(aggregateList.size(), objectUnderTest.get().size());
    }


    @Test
    public void getAggregateByID()
    {
        //Arrange
        Supplier<Stream<Integer>> counterSupplier = () -> Stream.of(100);
        counterSupplier.get()
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .forEach( objectUnderTest::add );

        //Act
        var aggregateList = counterSupplier.get()
                .map( key -> objectUnderTest.get(new JexxaValueObject(key)))
                .collect( Collectors.toList() );

        //Assert
        Assert.assertEquals(counterSupplier.get().count(), aggregateList.size());
    }

    @Test
    public void removeAggregate()
    {
        //Arrange
        Supplier<Stream<Integer>> counterSupplier = () -> Stream.of(100);
        counterSupplier.get()
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .forEach( objectUnderTest::add );

        //collect elements from Repository using get() which throws a runtime exception in case the element is not available   
        var aggregateList = counterSupplier.get()
                .map( key -> objectUnderTest.get(new JexxaValueObject(key)))
                .collect( Collectors.toList() );

        //Act
        aggregateList.forEach(objectUnderTest::remove);

        //Assert
        Assert.assertTrue(objectUnderTest.get().isEmpty());
    }


    @Test
    public void updateAggregate()
    {
        //Arrange
        int aggregateValue = 42;
        var counterStream = Stream.of(100);
        var aggregateList = counterStream
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .collect( Collectors.toList() );

        aggregateList.forEach(objectUnderTest::add);

        //Act
        aggregateList.forEach(element -> element.setInternalValue(aggregateValue));
        aggregateList.forEach(objectUnderTest::update);

        //Assert internal value is correctly set 
        objectUnderTest.get().forEach( element -> Assert.assertEquals(aggregateValue, element.getInternalValue()) );
    }

}
