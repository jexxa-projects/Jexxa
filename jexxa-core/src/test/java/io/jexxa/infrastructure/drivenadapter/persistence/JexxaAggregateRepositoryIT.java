package io.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jexxa.TestTags;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestTags.INTEGRATION_TEST)
class JexxaAggregateRepositoryIT
{

    static Stream<Properties> data() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCRepository.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCRepository.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCRepository.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCRepository.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCRepository.JDBC_AUTOCREATE, "true");
        postgresProperties.put(JDBCRepository.JDBC_DEFAULT_URL, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCRepository.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCRepository.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCRepository.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCRepository.JDBC_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCRepository.JDBC_AUTOCREATE, "true");
        h2Properties.put(JDBCRepository.JDBC_DEFAULT_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }


    @ParameterizedTest
    @MethodSource("data")
    void addAggregate(Properties repositoryProperties)
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
    void getAggregateByID(Properties repositoryProperties)
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
    void removeAggregate(Properties repositoryProperties)
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
    void updateAggregate(Properties repositoryProperties)
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
