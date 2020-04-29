package io.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Execution(ExecutionMode.SAME_THREAD)
@Tag("integration-test")
public class JexxaAggregateRepositoryIT
{

    public static Stream<Properties> data() {
        var properties = new Properties();
        properties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        properties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        properties.put(JDBCConnection.JDBC_AUTOCREATE, "true");
        properties.put(JDBCConnection.JDBC_DEFAULT_URL, "jdbc:postgresql://localhost:5432/postgres");

        return Stream.of(new Properties(), properties);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void addAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();

        var counterStream = Stream.of(100);
        var aggregateList = counterStream
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .collect( Collectors.toList() );

        //Act
        aggregateList.forEach(objectUnderTest::add);

        //Assert
        Assertions.assertEquals(aggregateList.size(), objectUnderTest.get().size());
    }


    @ParameterizedTest
    @MethodSource("data")
    public void getAggregateByID(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();

        Supplier<Stream<Integer>> counterSupplier = () -> Stream.of(100);
        counterSupplier.get()
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .forEach( objectUnderTest::add );

        //Act
        var aggregateList = counterSupplier.get()
                .map( key -> objectUnderTest.get(new JexxaValueObject(key)))
                .collect( Collectors.toList() );

        //Assert
        Assertions.assertEquals(counterSupplier.get().count(), aggregateList.size());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void removeAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();

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
        Assertions.assertTrue(objectUnderTest.get().isEmpty());
    }


    @ParameterizedTest
    @MethodSource("data")
    public void updateAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();

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
        objectUnderTest.get().forEach( element -> Assertions.assertEquals(aggregateValue, element.getInternalValue()) );
    }

}
