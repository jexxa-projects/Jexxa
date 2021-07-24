package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.JexxaObject.createCharSequence;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase.REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.optionalNumberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.stringComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.valueComparator;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

class IStringQueryIT
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

        // set internal int value to an ascending number
        // the internal string is set to  A, B, ..., AA, AB, ...
        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));

        testData.stream().limit(50).forEach(element -> element.setOptionalString(createCharSequence( element.getKey().getValue()))); // set internal
        testData.stream().limit(50).forEach( element -> element.setOptionalJexxaValue( element.getKey() )); // Set optional string value to A, B, ..., AA, AB, ...
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

        OPTIONAL_VALUE_OBJECT(optionalNumberComparator(element -> element.getOptionalJexxaValue().orElse(null), JexxaValueObject::getValue)),

        STRING_OBJECT(stringComparator(JexxaObject::getString));

        //TODO test optional string value

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
    void testComparisonOperator_STRING(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectMetadata.STRING_OBJECT, String.class);

        //Act
        var beginsWithA = objectUnderTest.beginsWith("A");
        var endsWithA = objectUnderTest.endsWith("A");
        var equalToA = objectUnderTest.isEqualTo("A");
        var includesA = objectUnderTest.includes("A");
        var notIncludesA = objectUnderTest.notIncludes("A");

        //Assert
        assertEquals(27, beginsWithA.size()); //A + AA..AZ = 27
        assertEquals(4, endsWithA.size());    // A + AA + BA + CA = 4
        assertEquals(1, equalToA.size());     // Only 1x A
        assertEquals(29, includesA.size());  // A + AA..AZ + BA + CA= 29
        assertEquals(71, notIncludesA.size());  // 100 - 29 (includesA.size()) = 71
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testAscending_STRING(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        var limit = 10;

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectMetadata.STRING_OBJECT, String.class);

        var expectedAscendingOrder = objectStore.get()
                .stream()
                .sorted(comparing(JexxaObject::getString))
                .collect(Collectors.toList());

        var expectedAscendingOrderLimit = expectedAscendingOrder
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        //Act
        var ascendingResult = objectUnderTest.getAscending();
        var ascendingLimitResult = objectUnderTest.getAscending(limit);

        //Assert
        assertEquals(expectedAscendingOrder, ascendingResult);
        assertEquals(expectedAscendingOrderLimit, ascendingLimitResult);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testDescending_STRING(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        var limit = 10;

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectMetadata.STRING_OBJECT, String.class);

        var expectedDescendingOrder = objectStore.get()
                .stream()
                .sorted(comparing(JexxaObject::getString).reversed())
                .collect(Collectors.toList());

        var expectedDescendingOrderLimit = expectedDescendingOrder
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        //Act
        var descendingResult = objectUnderTest.getDescending();
        var descendingResultLimit = objectUnderTest.getDescending(limit);

        //Assert
        assertEquals(expectedDescendingOrder, descendingResult);
        assertEquals(expectedDescendingOrderLimit, descendingResultLimit);
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
