package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.JexxaObject.createCharSequence;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase.REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numericTag;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.stringTag;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IStringQueryIT
{
    private static final int TEST_DATA_SIZE = 100;

    private List<JexxaObject> testData;
    private IObjectStore<JexxaObject, JexxaValueObject, JexxaObjectSchema> objectStore;

    /**
     * Defines the metadata that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaObjectSchema implements MetadataSchema
    {
        INT_VALUE(numericTag(JexxaObject::getInternalValue)),

        VALUE_OBJECT(numericTag(JexxaObject::getKey, JexxaValueObject::getValue)),

        OPTIONAL_VALUE_OBJECT(numericTag(JexxaObject::getOptionalValue, JexxaValueObject::getValue)),

        STRING_OBJECT(stringTag(JexxaObject::getString)),

        OPTIONAL_STRING_OBJECT(stringTag(JexxaObject::getOptionalString));

        /**
         *  Defines the constructor of the enum. Following code is equal for all object stores.
         */
        private final MetaTag<JexxaObject, ?, ? > metaTag;

        JexxaObjectSchema(MetaTag<JexxaObject,?, ?> metaTag)
        {
            this.metaTag = metaTag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public MetaTag<JexxaObject, ?, ?> getTag()
        {
            return metaTag;
        }
    }

    @BeforeEach
    void initTestData()
    {
        testData = IntStream.range(0, TEST_DATA_SIZE)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element)))
                .toList();

        // set internal int value to an ascending number
        // the internal string is set to  A, B, ..., AA, AB, ...
        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));

        testData.stream().limit(50).forEach(element -> element.setOptionalString(createCharSequence( element.getKey().getValue()))); // Set optional string in first 50 elements to A, B, ..., AA, AB, ...
        testData.stream().limit(50).forEach( element -> element.setOptionalValue( element.getKey() )); // Set optional values in first 50 elements to 0, .. , 49
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testStringComparisonOperator(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectSchema.STRING_OBJECT, String.class);

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
    void testComparisonOperatorOptionalString(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectSchema.OPTIONAL_STRING_OBJECT, String.class);

        //Act
        var beginsWithA = objectUnderTest.beginsWith("A");
        var endsWithA = objectUnderTest.endsWith("A");
        var equalToA = objectUnderTest.isEqualTo("A");
        var includesA = objectUnderTest.includes("A");
        var notIncludesA = objectUnderTest.notIncludes("A");
        var equalToNull = objectUnderTest.isNull();
        var notEqualToNull = objectUnderTest.isNotNull();

        //Assert
        assertEquals(24, beginsWithA.size()); //A + AA..AW = 24
        assertEquals(2, endsWithA.size());    // A + AA = 2
        assertEquals(1, equalToA.size());     // Only 1x A
        assertEquals(24, includesA.size());   // A + AA..AW = 24
        assertEquals(26, notIncludesA.size());  // 50 - 24 (includesA.size()) = 26
        assertEquals(50, equalToNull.size());
        assertEquals(50, notEqualToNull.size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testAscendingString(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        var limit = 10;

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectSchema.STRING_OBJECT, String.class);

        var expectedAscendingOrder = objectStore.get()
                .stream()
                .sorted(comparing(JexxaObject::getString))
                .toList();

        var expectedAscendingOrderLimit = expectedAscendingOrder
                .stream()
                .limit(10)
                .toList();

        //Act
        var ascendingResult = objectUnderTest.getAscending();
        var ascendingLimitResult = objectUnderTest.getAscending(limit);

        //Assert
        assertEquals(expectedAscendingOrder, ascendingResult);
        assertEquals(expectedAscendingOrderLimit, ascendingLimitResult);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testDescendingString(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        var limit = 10;

        var objectUnderTest = objectStore.getStringQuery( JexxaObjectSchema.STRING_OBJECT, String.class);

        var expectedDescendingOrder = objectStore.get()
                .stream()
                .sorted(comparing(JexxaObject::getString).reversed())
                .toList();

        var expectedDescendingOrderLimit = expectedDescendingOrder
                .stream()
                .limit(10)
                .toList();

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
            try(JDBCConnection jdbcConnection = new JDBCConnection(properties))
            {
                jdbcConnection.createTableCommand(JexxaObjectSchema.class)
                        .dropTableIfExists(JexxaObject.class)
                        .asIgnore();
            }
        }

        objectStore = ObjectStoreManager.getObjectStore(
                JexxaObject.class,
                JexxaObject::getKey,
                JexxaObjectSchema.class,
                properties);

        objectStore.removeAll();

        testData.forEach(objectStore::add);
    }
}
