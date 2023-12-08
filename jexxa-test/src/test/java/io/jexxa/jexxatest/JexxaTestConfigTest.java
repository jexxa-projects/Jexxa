package io.jexxa.jexxatest;

import io.jexxa.common.drivenadapter.messaging.MessageSenderManager;
import io.jexxa.common.drivenadapter.persistence.ObjectStoreManager;
import io.jexxa.common.drivenadapter.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetaTag;
import io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.common.drivenadapter.persistence.repository.imdb.IMDBRepository;
import io.jexxa.common.facade.jdbc.JDBCProperties;
import io.jexxa.common.facade.jms.JMSProperties;
import io.jexxa.infrastructure.persistence.objectstore.JexxaObject;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecordingStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.common.drivenadapter.persistence.RepositoryManager.getRepository;
import static io.jexxa.common.drivenadapter.persistence.objectstore.metadata.MetaTags.numericTag;
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
        var repository = getRepository(JexxaObject.class, JexxaObject::getKey, properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an IMDBRepository, independent of the Properties
        assertDoesNotThrow(() -> (IMDBRepository<JexxaObject, JexxaValueObject>) repository );
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void validateObjectStoreConfig(Properties properties)
    {
        //Arrange
        getJexxaTest(JexxaTestApplication.class);
        var objectStore = ObjectStoreManager.getObjectStore(JexxaObject.class, JexxaObject::getKey, JexxaObjectSchema.class, properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an IMDBObjectStore, independent of the Properties
        assertDoesNotThrow(() -> (IMDBObjectStore<JexxaObject, JexxaValueObject, JexxaObjectSchema>) objectStore );
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void validateMessageSenderConfig(Properties properties)
    {
        //Arrange
        getJexxaTest(JexxaTestApplication.class);
        var messageSender = MessageSenderManager.getMessageSender(JexxaTestConfigTest.class, properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an MessageRecordingStrategy, independent of the Properties
        assertDoesNotThrow(() -> (MessageRecordingStrategy) messageSender );
    }


    /**
     * Defines the metadata that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaObjectSchema implements MetadataSchema
    {
        @SuppressWarnings("unused")
        INT_VALUE(numericTag(JexxaObject::getInternalValue)),
        @SuppressWarnings("unused")
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


    @SuppressWarnings("unused")
    private static Stream<Properties> repositoryConfig() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCProperties.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCProperties.JDBC_PASSWORD, ADMIN);
        postgresProperties.put(JDBCProperties.JDBC_USERNAME, POSTGRES_USER);
        postgresProperties.put(JDBCProperties.JDBC_URL, "jdbc:postgresql://localhost:5432/objectstore");
        postgresProperties.put(JDBCProperties.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCProperties.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCProperties.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCProperties.JDBC_PASSWORD, ADMIN);
        h2Properties.put(JDBCProperties.JDBC_USERNAME, ADMIN);
        h2Properties.put(JDBCProperties.JDBC_URL, "jdbc:h2:mem:ComparableRepositoryTest;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCProperties.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }

    @SuppressWarnings("unused")
    private static Stream<Properties> messageSenderConfig() {
        var jmsProperties = new Properties();

        jmsProperties.put(JMSProperties.JNDI_FACTORY_KEY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jmsProperties.put(JMSProperties.JNDI_PROVIDER_URL_KEY, "tcp://localhost:61616");
        jmsProperties.put(JMSProperties.JNDI_PASSWORD_KEY, ADMIN);
        jmsProperties.put(JMSProperties.JNDI_USER_KEY, ADMIN);


        return Stream.of(new Properties(), jmsProperties);
    }

}
