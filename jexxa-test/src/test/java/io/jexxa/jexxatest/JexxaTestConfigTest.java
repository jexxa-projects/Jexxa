package io.jexxa.jexxatest;

import io.jexxa.common.drivenadapter.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetaTag;
import io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.common.drivenadapter.persistence.repository.imdb.IMDBRepository;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecordingStrategy;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.domain.model.JexxaAggregate;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.createMessageSender;
import static io.jexxa.common.drivenadapter.persistence.ObjectStoreFactory.createObjectStore;
import static io.jexxa.common.drivenadapter.persistence.RepositoryFactory.createRepository;
import static io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetaTags.numericTag;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcAutocreateDatabase;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcAutocreateTable;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcDriver;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcPassword;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUsername;
import static io.jexxa.common.facade.jms.JMSProperties.jndiFactoryKey;
import static io.jexxa.common.facade.jms.JMSProperties.jndiPasswordKey;
import static io.jexxa.common.facade.jms.JMSProperties.jndiProviderUrlKey;
import static io.jexxa.common.facade.jms.JMSProperties.jndiUserKey;
import static io.jexxa.jexxatest.JexxaTest.getJexxaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JexxaTestConfigTest
{
    public static final String REPOSITORY_CONFIG = "repositoryConfig";
    public static final String MESSAGE_SENDER_CONFIG = "messageSenderConfig";

    private static final String ADMIN = "admin";
    private static final String POSTGRES_USER = "postgres";



    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void validateRepositoryConfig(Properties properties)
    {
        //Arrange
        getJexxaTest(JexxaTestApplication.class);
        var repository = createRepository(JexxaAggregate.class, JexxaAggregate::getKey, properties);

        //Act / Assert: Since we initialized JexxaTest, we should always get an IMDBRepository, independent of the Properties
        assertDoesNotThrow(() -> (IMDBRepository<JexxaAggregate, JexxaValueObject>) repository );
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void validateObjectStoreConfig(Properties properties)
    {
        //Arrange
        getJexxaTest(JexxaTestApplication.class);
        var objectStore = createObjectStore(JexxaAggregate.class, JexxaAggregate::getKey, JexxaAggregateSchema.class, properties);

        //Act / Assert: Since we initialized JexxaTest, we should always get an IMDBObjectStore, independent of the Properties
        assertDoesNotThrow(() -> (IMDBObjectStore<JexxaAggregate, JexxaValueObject, JexxaAggregateSchema>) objectStore );
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void validateMessageSenderConfig(Properties properties)
    {
        //Arrange
        getJexxaTest(JexxaTestApplication.class);
        var messageSender = createMessageSender(JexxaTestConfigTest.class, properties);

        //Act / Assert: Since we initialized JexxaTest, we should always get a MessageRecordingStrategy, independent of the Properties
        assertDoesNotThrow(() -> (MessageRecordingStrategy) messageSender );
    }


    /**
     * Defines the metadata that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaAggregateSchema implements MetadataSchema
    {
        @SuppressWarnings("unused")
        INT_VALUE(numericTag(JexxaAggregate::getInternalValue)),
        @SuppressWarnings("unused")
        VALUE_OBJECT(numericTag(JexxaAggregate::getKey, JexxaValueObject::getValue));

        /**
         *  Defines the constructor of the enum. Following code is equal for all object stores.
         */
        private final MetaTag<JexxaAggregate, ?, ? > metaTag;

        JexxaAggregateSchema(MetaTag<JexxaAggregate,?, ?> metaTag)
        {
            this.metaTag = metaTag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public MetaTag<JexxaAggregate, ?, ?> getTag()
        {
            return metaTag;
        }
    }


    @SuppressWarnings("unused")
    private static Stream<Properties> repositoryConfig() {
        return Stream.of(new Properties(), getPostgresProperties(), getH2Properties());
    }

    private static Properties getPostgresProperties() {
        var postgresProperties = new Properties();
        postgresProperties.put(jdbcDriver(), "org.postgresql.Driver");
        postgresProperties.put(jdbcPassword(), ADMIN);
        postgresProperties.put(jdbcUsername(), POSTGRES_USER);
        postgresProperties.put(jdbcUrl(), "jdbc:postgresql://localhost:5432/objectstore");
        postgresProperties.put(jdbcAutocreateTable(), "true");
        postgresProperties.put(jdbcAutocreateDatabase(), "jdbc:postgresql://localhost:5432/postgres");
        return postgresProperties;
    }

    private static Properties getH2Properties() {
        var h2Properties = new Properties();
        h2Properties.put(jdbcDriver(), "org.h2.Driver");
        h2Properties.put(jdbcPassword(), ADMIN);
        h2Properties.put(jdbcUsername(), ADMIN);
        h2Properties.put(jdbcUrl(), "jdbc:h2:mem:ComparableRepositoryTest;DB_CLOSE_DELAY=-1");
        h2Properties.put(jdbcAutocreateTable(), "true");
        return h2Properties;
    }

    @SuppressWarnings("unused")
    private static Stream<Properties> messageSenderConfig() {
        var jmsProperties = new Properties();

        jmsProperties.put(jndiFactoryKey(), "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jmsProperties.put(jndiProviderUrlKey(), "tcp://localhost:61616");
        jmsProperties.put(jndiPasswordKey(), ADMIN);
        jmsProperties.put(jndiUserKey(), ADMIN);


        return Stream.of(new Properties(), jmsProperties);
    }

}
