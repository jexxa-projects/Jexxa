package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.optionalNumberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.valueComparator;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.StringComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class IStringQueryIT
{
    private static final String REPOSITORY_CONFIG = "repositoryConfig";
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

        testData.forEach(element -> element.setOptionalString(createCharSequence( element.getKey().getValue()))); // set internal int value to an ascending number

        testData.stream().limit(50).forEach( element -> element.setOptionalJexxaValue( element.getKey() )); // Set optional string value to A, B, ..., AA, AB, ...
    }

    /**
     * Defines the meta data that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    public enum JexxaObjectMetadata implements MetadataComparator
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

        STRING_OBJECT(new StringComparator<>(JexxaObject::getString, element -> element));

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

    @SuppressWarnings("unused")
    static Stream<Properties> repositoryConfig() {
       /* var postgresProperties = new Properties();
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

        return Stream.of(new Properties(), postgresProperties, h2Properties);*/
        return Stream.of(new Properties());
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

    private static String createCharSequence(int n) {
        var counter = n;
        char[] buf = new char[(int) floor(log(25 * (counter + 1)) / log(26))];
        for (int i = buf.length - 1; i >= 0; i--)
        {
            counter--;
            buf[i] = (char) ('A' + counter % 26);
            counter /= 26;
        }
        return new String(buf);
    }
}
