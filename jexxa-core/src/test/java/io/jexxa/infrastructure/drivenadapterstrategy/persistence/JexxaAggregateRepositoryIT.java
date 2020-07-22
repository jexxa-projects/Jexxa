package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JexxaAggregateRepositoryIT
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";

    private List<JexxaAggregate> aggregateList;

    @SuppressWarnings("unused")
    static Stream<Properties> repositoryConfig() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCKeyValueRepository.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCKeyValueRepository.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCKeyValueRepository.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCKeyValueRepository.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCKeyValueRepository.JDBC_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }


    @BeforeEach
    void initTests()
    {
        aggregateList = Stream.of(100)
                .map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) )
                .collect( toList() );
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void addAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();

        //Act
        aggregateList.forEach(objectUnderTest::add);

        //Assert
        assertEquals(aggregateList.size(), objectUnderTest.get().size());
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void getAggregateByID(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);

        //Act
        var resultList = aggregateList.stream()
                .map( aggregate -> objectUnderTest.get(aggregate.getKey()))
                .collect( toList() );

        //Assert
        assertEquals(aggregateList.size(), resultList.size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void removeAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);

        //collect elements from Repository using get() which throws a runtime exception in case the element is not available
        var resultList = objectUnderTest.get();

        //Act
        resultList.forEach(objectUnderTest::remove);

        //Assert
        assertTrue(objectUnderTest.get().isEmpty());
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void updateAggregate(Properties repositoryProperties)
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);

        int aggregateValue = 42;

        //Act
        aggregateList.forEach(element -> element.setInternalValue(aggregateValue));
        aggregateList.forEach(objectUnderTest::update);

        //Assert internal value is correctly set 
        objectUnderTest.get().forEach( element -> assertEquals(aggregateValue, element.getInternalValue()) );
    }

}
