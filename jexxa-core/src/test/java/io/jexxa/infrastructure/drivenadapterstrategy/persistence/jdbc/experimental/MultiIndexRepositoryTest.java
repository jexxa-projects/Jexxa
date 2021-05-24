package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MultiIndexRepositoryTest
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";

    /**
     * Defines the Range comparators that we use:
     * Conventions for real databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    public enum SearchStrategies implements SearchStrategy
    {
        KEY(null),
        VALUE(null),

        INTERNAL_VALUE(RangeComparators.createNumberComparator(JexxaAggregate::getInternalValue)),

        AGGREGATE_KEY(RangeComparators.create(
                aggregate -> aggregate.getKey().getValue(),
                JexxaValueObject::getValue));

        private final RangeComparator<JexxaAggregate,? > supplier;


        SearchStrategies(final RangeComparator<JexxaAggregate,?> supplier)
        {
            this.supplier = supplier;
        }

        @Override
        @SuppressWarnings("unchecked")
        public RangeComparator<JexxaAggregate, ?> get()
        {
            return supplier;
        }
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testCompareInternalValue(Properties properties)
    {
        //Arrange
        var testData = IntStream.range(1, 100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());


        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);

        objectUnderTest.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);

        IRangeQuery<JexxaAggregate, Integer> rangeQuery = objectUnderTest.getRangeQuery( SearchStrategies.INTERNAL_VALUE);

        //Act
        var fromResult = rangeQuery.getFrom(50);
        var untilResult = rangeQuery.getUntil(50);
        var rangedResult = rangeQuery.getRange(30,50);

        //Assert
        assertEquals(50, fromResult.size());
        assertEquals(50, untilResult.size());
        assertEquals(21, rangedResult.size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testCompareAggregateKey(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();

        var testData = IntStream.range(1, 100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);
        //testData.forEach(objectUnderTest::update);

        IRangeQuery<JexxaAggregate, JexxaValueObject> irangedResult = objectUnderTest.getRangeQuery( SearchStrategies.AGGREGATE_KEY);

        //Act
        var fromResult = irangedResult.getFrom(new JexxaValueObject(50));
        var untilResult = irangedResult.getUntil(new JexxaValueObject(50));
        var rangedResult = irangedResult.getRange(new JexxaValueObject(30),new JexxaValueObject(50));

        //Assert
        assertEquals(50, fromResult.size());
        assertEquals(50, untilResult.size());
        assertEquals(21, rangedResult.size());
    }

    @SuppressWarnings("unused")
    static Stream<Properties> repositoryConfig() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCConnection.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/multiindexrepository");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCConnection.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCConnection.JDBC_URL, "jdbc:h2:mem:ComparableRepositoryTest;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }
}
