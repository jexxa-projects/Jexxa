package io.jexxa.jexxatest;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numberTag;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.JexxaObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecordingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("unused")
class JexxaTestConfigTest
{
    public static final String REPOSITORY_CONFIG = "repositoryConfig";
    public static final String MESSAGE_SENDER_CONFIG = "messageSenderConfig";

    @SuppressWarnings("FieldCanBeLocal")
    private JexxaTest jexxaTest;


    @BeforeEach
    void setUp()
    {
        //Arrange
        var jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToApplicationCore("io.jexxa.application.domainservice")
                .addToInfrastructure("io.jexxa.application.infrastructure");

        jexxaTest = new JexxaTest(jexxaMain);
    }


    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void validateRepositoryConfig(Properties properties)
    {
        //Arrange
        var repository = RepositoryManager.getRepository(JexxaObject.class, JexxaObject::getKey, properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an IMDBRepository, independent of the Properties
        assertDoesNotThrow(() -> (IMDBRepository<JexxaObject, JexxaValueObject>) repository );
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void validateObjectStoreConfig(Properties properties)
    {
        //Arrange
        var objectStore = ObjectStoreManager.getObjectStore(JexxaObject.class, JexxaObject::getKey, JexxaObjectSchema.class, properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an IMDBObjectStore, independent of the Properties
        assertDoesNotThrow(() -> (IMDBObjectStore<JexxaObject, JexxaValueObject, JexxaObjectSchema>) objectStore );
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void validateMessageSenderConfig(Properties properties)
    {
        //Arrange
        var messageSender = MessageSenderManager.getMessageSender(properties);

        //Act / Assert : Since we initialized JexxaTest, we should always get an MessageRecordingStrategy, independent of the Properties
        assertDoesNotThrow(() -> (MessageRecordingStrategy) messageSender );
    }


    /**
     * Defines the meta data that we use:
     * Conventions for databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    private enum JexxaObjectSchema implements MetadataSchema
    {
        INT_VALUE(numberTag(JexxaObject::getInternalValue)),

        VALUE_OBJECT(numberTag(JexxaObject::getKey, JexxaValueObject::getValue));

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

    private static Stream<Properties> messageSenderConfig() {
        var jmsProperties = new Properties();

        jmsProperties.put(JMSSender.JNDI_FACTORY_KEY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jmsProperties.put(JMSSender.JNDI_PROVIDER_URL_KEY, "tcp://localhost:61616");
        jmsProperties.put(JMSSender.JNDI_PASSWORD_KEY, "admin");
        jmsProperties.put(JMSSender.JNDI_USER_KEY, "admin");


        return Stream.of(new Properties(), jmsProperties);
    }

}
