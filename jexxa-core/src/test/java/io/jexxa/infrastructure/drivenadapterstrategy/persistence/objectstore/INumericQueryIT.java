package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.valueComparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class INumericQueryIT
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";
    private static final int TEST_DATA_SIZE = 100;

    private List<JexxaAggregate> testData;
    private IObjectStore<JexxaAggregate, JexxaValueObject, JexxaAggregateMetadata> objectStore;

    @BeforeEach
    void initTest()
    {
        testData = IntStream.range(0, TEST_DATA_SIZE)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
    }

    /**
     * Defines the meta data that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    public enum JexxaAggregateMetadata implements MetadataComparator
    {
        /**
         * Defines the meta data including comparator to query the object store.
         * The following information is specific to your implementation and must be adjusted.
         */
        KEY(keyComparator()),

        VALUE(valueComparator()),

        INT_VALUE(numberComparator(JexxaAggregate::getInternalValue)),

        VALUE_OBJECT(numberComparator(JexxaAggregate::getKey, JexxaValueObject::getValue));

        /**
         *  Defines the constructor of the enum. Following code is equal for all object stores.
         */
        private final Comparator<JexxaAggregate, ?, ? > comparator;

        JexxaAggregateMetadata(Comparator<JexxaAggregate,?, ?> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<JexxaAggregate, ?, ?> getComparator()
        {
            return comparator;
        }
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testComparisonOperators_INT_VALUE(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.INT_VALUE);

        var greaterOrEqualThanExpected = IntStream.range(50,100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());
        var lessOrEqualThanThanExpected = IntStream.rangeClosed(0,50)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var greaterThanExpected = IntStream.range(51, 100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessThanExpected = IntStream.range(0,50).
                mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeClosedExpected = IntStream.rangeClosed(30,50).
                mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeExpected = IntStream.range(30,50).
                mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());

        //Act
        var greaterOrEqualThan = objectUnderTest.isGreaterOrEqualThan(50);
        var lessOrEqualThan = objectUnderTest.isLessOrEqualThan(50);
        var greaterThan = objectUnderTest.isGreaterThan(50);
        var lessThan = objectUnderTest.isLessThan(50);
        var rangeClosed = objectUnderTest.getRangeClosed(30,50);
        var range = objectUnderTest.getRange(30,50);

        //Assert
        assertEquals(greaterOrEqualThanExpected, greaterOrEqualThan);
        assertEquals(greaterThanExpected, greaterThan);

        assertEquals(lessThanExpected, lessThan);
        assertEquals(lessOrEqualThanThanExpected, lessOrEqualThan);

        assertEquals(rangeClosedExpected, rangeClosed);
        assertEquals(rangeExpected, range);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testComparisonOperator_VALUE_OBJECT(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.VALUE_OBJECT);

        var greaterOrEqualThanExpected = IntStream
                .range(50,100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessOrEqualThanThanExpected = IntStream.rangeClosed(0,50)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var greaterThanExpected = IntStream.range(51, 100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessThanExpected = IntStream.range(0,50)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeClosedExpected = IntStream.rangeClosed(30,50)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeExpected = IntStream.range(30,50)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element))).collect(Collectors.toList());


        //Act
        var greaterOrEqualThan = objectUnderTest.isGreaterOrEqualThan(new JexxaValueObject(50));
        var lessOrEqualThan = objectUnderTest.isLessOrEqualThan(new JexxaValueObject(50));
        var greaterThan = objectUnderTest.isGreaterThan(new JexxaValueObject(50));
        var lessThan = objectUnderTest.isLessThan(new JexxaValueObject(50));
        var rangeClosed = objectUnderTest.getRangeClosed(new JexxaValueObject(30),new JexxaValueObject(50));
        var range = objectUnderTest.getRange(new JexxaValueObject(30),new JexxaValueObject(50));

        //Assert
        assertEquals(greaterOrEqualThanExpected, greaterOrEqualThan);
        assertEquals(greaterThanExpected, greaterThan);

        assertEquals(lessThanExpected, lessThan);
        assertEquals(lessOrEqualThanThanExpected, lessOrEqualThan);

        assertEquals(rangeClosedExpected, rangeClosed);
        assertEquals(rangeExpected, range);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetAscending(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.INT_VALUE);
        var expectedResult = testData.stream()
                .sorted(java.util.Comparator.comparing( JexxaAggregate::getInternalValue))
                .collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getAscending();

        //Assert
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetAscendingWithLimit(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.INT_VALUE);
        var limitAmount = 10 ;
        var expectedResult = testData.stream()
                .sorted(java.util.Comparator.comparing( JexxaAggregate::getInternalValue))
                .limit(limitAmount).collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getAscending(limitAmount);

        //Assert
        assertEquals(limitAmount, result.size());
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetDescending(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.INT_VALUE);
        var expectedResult = testData.stream()
                .sorted(java.util.Comparator.comparing( JexxaAggregate::getInternalValue).reversed())
                .collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getDescending();

        //Assert
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetDescendingWithLimit(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaAggregateMetadata.INT_VALUE);
        var limitAmount = 10 ;
        var expectedResult = testData.stream()
                .sorted(java.util.Comparator.comparing( JexxaAggregate::getInternalValue).reversed())
                .limit(limitAmount).collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getDescending(limitAmount);

        //Assert
        assertEquals(limitAmount, result.size());
        assertEquals(expectedResult, result);
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

    void initObjectStore(Properties properties)
    {
        if (!properties.isEmpty())
        {
            var jdbcConnection = new JDBCConnection(properties);
            jdbcConnection.createTableCommand(JexxaAggregateMetadata.class)
                    .dropTableIfExists(JexxaAggregate.class)
                    .asIgnore();
        }

        objectStore = ObjectStoreManager.getObjectStore(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                JexxaAggregateMetadata.class,
                properties);

        objectStore.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectStore::add);
    }
}
