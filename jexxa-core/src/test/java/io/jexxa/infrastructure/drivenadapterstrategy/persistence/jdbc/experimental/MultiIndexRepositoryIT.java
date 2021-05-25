package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class MultiIndexRepositoryIT
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";
    private static final int TEST_DATA_SIZE = 100;

    private List<JexxaAggregate> testData;

    @BeforeEach
    void initTest()
    {
        testData = IntStream.range(0, TEST_DATA_SIZE)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
    }

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
    void testAddOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();


        //Act
        testData.forEach(objectUnderTest::add);

        //Assert
        assertEquals(TEST_DATA_SIZE, objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testDeleteAllOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();

        testData.forEach(objectUnderTest::add);

        //Act
        objectUnderTest.removeAll();

        //Assert
        assertEquals(0, objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testDeleteOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();
        testData.forEach(objectUnderTest::add);

        //Act
        objectUnderTest.remove(testData.stream().findFirst().orElseThrow().getKey());

        //Assert
        assertEquals(TEST_DATA_SIZE - 1 , objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testReadAllOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();

        testData.forEach(objectUnderTest::add);

        //Act
        var result = objectUnderTest.get();
        result.sort(Comparator.comparing(element -> element.getKey().getValue()));

        //Assert
        assertEquals(testData, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testReadOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();

        testData.forEach(objectUnderTest::add);

        //Act
        var result = objectUnderTest.get(testData.get(0).getKey());

        //Assert
        assertTrue(result.isPresent());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testUpdateOperation(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);
        objectUnderTest.removeAll();
        testData.forEach(objectUnderTest::add);

        //Act
        testData.forEach(element -> element.setInternalValue(TEST_DATA_SIZE));
        testData.forEach(objectUnderTest::update);
        var result = objectUnderTest.get();

        //Assert
        assertTrue(result.stream().allMatch( element -> element.getInternalValue() == TEST_DATA_SIZE));
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testCompareInternalValue(Properties properties)
    {
        //Arrange
        var objectUnderTest = MultiIndexRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                SearchStrategies.class,
                properties);

        objectUnderTest.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);

        var subset = objectUnderTest.getSubset( SearchStrategies.INTERNAL_VALUE);

        //Act
        var fromResult = subset.getFrom(50);
        var untilResult = subset.getUntil(50);
        var rangedResult = subset.getRange(30,50);

        //Assert
        assertEquals(50, fromResult.size());
        assertEquals(51, untilResult.size());
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

        testData.forEach(objectUnderTest::add);
        //testData.forEach(objectUnderTest::update);

        ISubset<JexxaAggregate, JexxaValueObject> irangedResult = objectUnderTest.getSubset( SearchStrategies.AGGREGATE_KEY);

        //Act
        var fromResult = irangedResult.getFrom(new JexxaValueObject(50));
        var untilResult = irangedResult.getUntil(new JexxaValueObject(50));
        var rangedResult = irangedResult.getRange(new JexxaValueObject(30),new JexxaValueObject(50));

        //Assert
        assertEquals(50, fromResult.size());
        assertEquals(51, untilResult.size());
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
