package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase.REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numericTag;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class IObjectStoreIT
{
    private static final int TEST_DATA_SIZE = 100;

    private List<JexxaObject> testData;
    private IObjectStore<JexxaObject, JexxaValueObject, JexxaObjectSchema> objectUnderTest;

    @BeforeEach
    void initTest()
    {
        testData = IntStream.range(0, TEST_DATA_SIZE)
                .mapToObj(element -> JexxaObject.create(new JexxaValueObject(element)))
                .toList();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
    }

    /**
     * Defines the metadata that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaObjectSchema implements MetadataSchema
    {
        INT_VALUE(numericTag(JexxaObject::getInternalValue)),

        VALUE_OBJECT(numericTag(JexxaObject::getKey, JexxaValueObject::getValue));

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

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testAdd(Properties properties)
    {
        //Arrange
        initObjectStore(properties);
        objectUnderTest.removeAll();

        //Act
        testData.forEach(objectUnderTest::add);

        //Assert
        assertEquals(TEST_DATA_SIZE, objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testRemoveAll(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        //Act
        objectUnderTest.removeAll();

        //Assert
        assertEquals(0, objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testRemove(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        var elementToRemove = testData.stream()
                .findFirst()
                .orElseThrow()
                .getKey();

        //Act
        objectUnderTest.remove(elementToRemove);

        //Assert
        assertTrue(objectUnderTest.get(elementToRemove).isEmpty());
        assertEquals(TEST_DATA_SIZE - 1 , objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGetAll(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        //Act
        var result = objectUnderTest.get();
        result = result.stream()
                .sorted( comparing(element -> element.getKey().getValue()))
                .toList();

        //Assert
        assertEquals(testData, result);
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testGet(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        //Act
        var result = objectUnderTest.get(testData.get(0).getKey());

        //Assert
        assertTrue(result.isPresent());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testUpdate(Properties properties)
    {
        //Arrange
        initObjectStore(properties);

        //Act
        testData.forEach(element -> element.setInternalValue(TEST_DATA_SIZE));
        testData.forEach(objectUnderTest::update);
        var result = objectUnderTest.get();

        //Assert
        assertTrue(result.stream().allMatch( element -> element.getInternalValue() == TEST_DATA_SIZE));
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

        objectUnderTest = ObjectStoreManager.getObjectStore(
                JexxaObject.class,
                JexxaObject::getKey,
                JexxaObjectSchema.class,
                properties);

        objectUnderTest.removeAll();

        testData.forEach(element -> element.setInternalValue(element.getKey().getValue()));
        testData.forEach(objectUnderTest::add);
    }
}
