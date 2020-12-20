package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ComparableRepositoryTest
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";

    public enum SearchStrategies implements Strategy
    {
        INTERNAL_VALUE(() -> new Comparators.NumberComparator<JexxaAggregate, Integer>(
                JexxaAggregate::getInternalValue)),

        AGGREGATE_KEY(() -> new Comparator<>(
                (aggregate) -> aggregate.getKey().getValue(),
                JexxaValueObject::getValue));

        private final Supplier< Comparator<JexxaAggregate,?>> supplier;


        SearchStrategies(final Supplier< Comparator<JexxaAggregate,?>> supplier) {
            this.supplier = supplier;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<JexxaAggregate, ? > getStrategy()
        {
            return supplier.get();
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


        var objectUnderTest = ComparableRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                EnumSet.allOf(SearchStrategies.class),
                properties);

        objectUnderTest.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);

        IRangedResult<JexxaAggregate, Integer> irangedResult = objectUnderTest.getRangeInterface( SearchStrategies.INTERNAL_VALUE);

        //Act
        var fromResult = irangedResult.getFrom(50);
        var untilResult = irangedResult.getUntil(50);
        var rangedResult = irangedResult.getRange(30,50);

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
        var testData = IntStream.range(1, 100)
                .mapToObj(element -> JexxaAggregate.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());


        var objectUnderTest = ComparableRepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                EnumSet.allOf(SearchStrategies.class),
                properties);

        objectUnderTest.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);

        IRangedResult<JexxaAggregate, JexxaValueObject> irangedResult = objectUnderTest.getRangeInterface( SearchStrategies.AGGREGATE_KEY);

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
        postgresProperties.put(JDBCKeyValueRepository.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_URL, "jdbc:postgresql://localhost:5432/comparablerepository");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCKeyValueRepository.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCKeyValueRepository.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCKeyValueRepository.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCKeyValueRepository.JDBC_URL, "jdbc:h2:mem:ComparableRepositoryTest;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCKeyValueRepository.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }
}
