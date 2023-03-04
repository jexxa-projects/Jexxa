package io.jexxa.infrastructure.persistence.repository;

import io.jexxa.TestConstants;
import io.jexxa.testapplication.domain.model.JexxaEntity;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.infrastructure.drivenadapter.persistence.JexxaEntityRepositoryImpl;
import io.jexxa.common.wrapper.jdbc.JDBCTestDatabase;
import io.jexxa.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.infrastructure.persistence.repository.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JexxaEntityRepositoryImplIT
{
    private List<JexxaEntity> aggregateList;
    private static final String ALL_REPOSITORY_CONFIGS = "repositoryConfig";


    @BeforeEach
    void initTests()
    {
        aggregateList = IntStream.range(1, 100)
                .mapToObj(element -> JexxaEntity.create(new JexxaValueObject(element)))
                .toList();
    }

    @ParameterizedTest
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void addAggregate(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
        objectUnderTest.removeAll();

        //Act
        aggregateList.forEach(objectUnderTest::add);

        //Assert
        assertEquals(aggregateList.size(), objectUnderTest.get().size());
    }

    @ParameterizedTest
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void testPreconditionAddAggregate(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);
        var firstElement = aggregateList.get(0);

        //Act / Assert
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.add(firstElement));
    }


    @ParameterizedTest
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void getAggregateByID(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);

        //Act
        var resultList = aggregateList.stream()
                .map(aggregate -> objectUnderTest.get(aggregate.getKey()))
                .toList();

        //Assert
        assertEquals(aggregateList.size(), resultList.size());
    }

    @ParameterizedTest
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void removeAggregate(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
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
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void testPreconditionRemoveAggregate(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
        objectUnderTest.removeAll();

        aggregateList.forEach(objectUnderTest::add);
        var firstElement = aggregateList.get(0);

        //Act / Assert
        objectUnderTest.removeAll();
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.remove(firstElement));
    }


    @ParameterizedTest
    @MethodSource(ALL_REPOSITORY_CONFIGS)
    void updateAggregate(Properties repositoryProperties)
    {
        //Arrange
        dropTable(repositoryProperties);
        var objectUnderTest = new JexxaEntityRepositoryImpl(repositoryProperties);
        objectUnderTest.removeAll();
        aggregateList.forEach(objectUnderTest::add);

        int aggregateValue = 42;

        //Act
        aggregateList.forEach(element -> element.setInternalValue(aggregateValue));
        aggregateList.forEach(objectUnderTest::update);

        //Assert internal value is correctly set
        objectUnderTest.get().forEach(element -> assertEquals(aggregateValue, element.getInternalValue()));
    }

    @SuppressWarnings("unused")
    static Stream<Properties> repositoryConfig()
    {
        return Stream.concat(Stream.of(new Properties()), JDBCTestDatabase.repositoryConfigJDBC());
    }

    private void dropTable(Properties properties)
    {
        if (!properties.isEmpty()) {
            try ( JDBCConnection connection = new JDBCConnection(properties) ) {
                connection.createTableCommand(JDBCKeyValueRepository.KeyValueSchema.class)
                        .dropTableIfExists(JexxaEntity.class)
                        .asIgnore();
            }
        }
    }

}