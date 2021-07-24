package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase.REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.optionalNumberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.valueComparator;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class INumericQueryIT
{
    private static final int TEST_DATA_SIZE = 100;

    private List<JexxaObject> testData;
    private IObjectStore<JexxaObject, JexxaValueObject, JexxaObjectMetadata> objectStore;

    @BeforeEach
    void initTestData()
    {
        testData = IntStream.range(0, TEST_DATA_SIZE)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue())); // set internal int value to an ascending number

        testData.stream().limit(50).forEach( element -> element.setOptionalValue( element.getKey() )); // Set optional value to half ot the test data (0 to 49)
    }

    /**
     * Defines the meta data that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaObjectMetadata implements MetadataComparator
    {
        /**
         * Defines the meta data including comparator to query the object store.
         * The following information is specific to your implementation and must be adjusted.
         */
        KEY(keyComparator()),

        VALUE(valueComparator()),

        INT_VALUE(numberComparator(JexxaObject::getInternalValue)),

        VALUE_OBJECT(numberComparator(JexxaObject::getKey, JexxaValueObject::getValue)),

        OPTIONAL_VALUE_OBJECT(optionalNumberComparator(JexxaObject::getOptionalValue, JexxaValueObject::getValue));

        /**
         *  Defines the constructor of the enum. Following code is equal for all object stores.
         */
        private final Comparator<JexxaObject, ?, ? > comparator;

        JexxaObjectMetadata(Comparator<JexxaObject,?, ?> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<JexxaObject, ?, ?> getComparator()
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
        var objectUnderTest = objectStore. getNumericQuery( JexxaObjectMetadata.INT_VALUE, Integer.class);

        var greaterOrEqualThanExpected = IntStream.range(50,100)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element)))
                .collect(Collectors.toList());
        var lessOrEqualThanThanExpected = IntStream.rangeClosed(0,50)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var greaterThanExpected = IntStream.range(51, 100)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessThanExpected = IntStream.range(0,50).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeClosedExpected = IntStream.rangeClosed(30,50).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeExpected = IntStream.range(30,50).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());

        var equalToExpected = IntStream.rangeClosed(0,0).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var notEqualToExpected = IntStream.range(1,100).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());

        //Act
        var greaterOrEqualThan = objectUnderTest.isGreaterOrEqualThan(50);
        var lessOrEqualThan = objectUnderTest.isLessOrEqualThan(50);
        var greaterThan = objectUnderTest.isGreaterThan(50);
        var lessThan = objectUnderTest.isLessThan(50);
        var rangeClosed = objectUnderTest.getRangeClosed(30,50);
        var range = objectUnderTest.getRange(30,50);
        var equalTo = objectUnderTest.isEqualTo(0);
        var notEqualTo = objectUnderTest.isNotEqualTo(0);

        //Assert
        assertEquals(greaterOrEqualThanExpected, greaterOrEqualThan);
        assertEquals(greaterThanExpected, greaterThan);

        assertEquals(lessThanExpected, lessThan);
        assertEquals(lessOrEqualThanThanExpected, lessOrEqualThan);

        assertEquals(rangeClosedExpected, rangeClosed);
        assertEquals(rangeExpected, range);

        assertEquals(notEqualToExpected, notEqualTo);
        assertEquals(equalToExpected, equalTo);
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testComparisonOperator_VALUE_OBJECT(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.VALUE_OBJECT, JexxaValueObject.class);

        var greaterOrEqualThanExpected = IntStream
                .range(50,100)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessOrEqualThanThanExpected = IntStream.rangeClosed(0,50)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var greaterThanExpected = IntStream.range(51, 100)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessThanExpected = IntStream.range(0,50)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeClosedExpected = IntStream.rangeClosed(30,50)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeExpected = IntStream.range(30,50)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var equalToExpected = IntStream.rangeClosed(0,0).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var notEqualToExpected = IntStream.range(1,100).
                mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());


        //Act
        var greaterOrEqualThan = objectUnderTest.isGreaterOrEqualThan(new JexxaValueObject(50));
        var lessOrEqualThan = objectUnderTest.isLessOrEqualThan(new JexxaValueObject(50));
        var greaterThan = objectUnderTest.isGreaterThan(new JexxaValueObject(50));
        var lessThan = objectUnderTest.isLessThan(new JexxaValueObject(50));
        var rangeClosed = objectUnderTest.getRangeClosed(new JexxaValueObject(30),new JexxaValueObject(50));
        var range = objectUnderTest.getRange(new JexxaValueObject(30),new JexxaValueObject(50));
        var equalTo = objectUnderTest.isEqualTo(new JexxaValueObject(0));
        var notEqualTo = objectUnderTest.isNotEqualTo(new JexxaValueObject(0));

        //Assert
        assertEquals(greaterOrEqualThanExpected, greaterOrEqualThan);
        assertEquals(greaterThanExpected, greaterThan);

        assertEquals(lessThanExpected, lessThan);
        assertEquals(lessOrEqualThanThanExpected, lessOrEqualThan);

        assertEquals(rangeClosedExpected, rangeClosed);
        assertEquals(rangeExpected, range);

        assertEquals(notEqualToExpected, notEqualTo);
        assertEquals(equalToExpected, equalTo);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testComparisonOperator_OPTIONAL_VALUE_OBJECT(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.OPTIONAL_VALUE_OBJECT, JexxaValueObject.class);

        var lessOrEqualThanThanExpected = IntStream.rangeClosed(0,49)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var lessThanExpected = IntStream.rangeClosed(0,49)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeClosedExpected = IntStream.rangeClosed(30,49)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var rangeExpected = IntStream.rangeClosed(30,49)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var isNotNullExpected = IntStream.rangeClosed(0,49)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());
        var isNullExpected = IntStream.range(50,100)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element))).collect(Collectors.toList());

        //Act
        var greaterOrEqualThan = objectUnderTest.isGreaterOrEqualThan(new JexxaValueObject(50));
        var lessOrEqualThan = objectUnderTest.isLessOrEqualThan(new JexxaValueObject(50));
        var greaterThan = objectUnderTest.isGreaterThan(new JexxaValueObject(50));
        var lessThan = objectUnderTest.isLessThan(new JexxaValueObject(50));
        var rangeClosed = objectUnderTest.getRangeClosed(new JexxaValueObject(30),new JexxaValueObject(50));
        var range = objectUnderTest.getRange(new JexxaValueObject(30),new JexxaValueObject(50));
        var isNull = objectUnderTest.isNull();
        var isNotNull = objectUnderTest.isNotNull();

        //Assert
        assertEquals(Collections.emptyList(), greaterOrEqualThan);
        assertEquals(Collections.emptyList(), greaterThan);

        assertEquals(lessThanExpected, lessThan);
        assertEquals(lessOrEqualThanThanExpected, lessOrEqualThan);

        assertEquals(rangeClosedExpected, rangeClosed);
        assertEquals(rangeExpected, range);

        assertEquals(isNullExpected, isNull);
        assertEquals(isNotNullExpected, isNotNull);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetAscending(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.INT_VALUE, Integer.class);
        var expectedResult = testData.stream()
                .sorted(comparing( JexxaObject::getInternalValue))
                .collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getAscending();

        //Assert
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetAscendingWithOptionalValue(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.OPTIONAL_VALUE_OBJECT, JexxaValueObject.class);

        var expectedResult = testData.stream()
                .sorted(comparing( JexxaObject::getInternalValue))
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

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.INT_VALUE, Integer.class);
        var limitAmount = 10 ;
        var expectedResult = testData.stream()
                .sorted(comparing( JexxaObject::getInternalValue))
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

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.INT_VALUE, Integer.class);
        var expectedResult = testData.stream()
                .sorted(comparing( JexxaObject::getInternalValue).reversed())
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

        var objectUnderTest = objectStore.getNumericQuery( JexxaObjectMetadata.INT_VALUE, Integer.class);
        var limitAmount = 10 ;
        var expectedResult = testData.stream()
                .sorted(comparing( JexxaObject::getInternalValue).reversed())
                .limit(limitAmount).collect(Collectors.toList());

        //Act
        var result = objectUnderTest.getDescending(limitAmount);

        //Assert
        assertEquals(limitAmount, result.size());
        assertEquals(expectedResult, result);
    }

    void initObjectStore(Properties properties)
    {
        if (!properties.isEmpty())
        {
            var jdbcConnection = new JDBCConnection(properties);
            jdbcConnection.createTableCommand(JexxaObjectMetadata.class)
                    .dropTableIfExists(JexxaObject.class)
                    .asIgnore();
        }

        objectStore = ObjectStoreManager.getObjectStore(
                JexxaObject.class,
                JexxaObject::getKey,
                JexxaObjectMetadata.class,
                properties);

        objectStore.removeAll();

        testData.forEach(objectStore::add);
    }
}
