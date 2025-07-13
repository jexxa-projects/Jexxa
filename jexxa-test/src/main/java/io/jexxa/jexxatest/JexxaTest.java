package io.jexxa.jexxatest;

import io.jexxa.common.drivenadapter.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.common.drivenadapter.persistence.repository.imdb.IMDBRepository;
import io.jexxa.common.facade.utils.annotation.CheckReturnValue;
import io.jexxa.common.facade.utils.function.ThrowingConsumer;
import io.jexxa.core.JexxaMain;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecorder;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecorderManager;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecordingStrategy;

import java.util.Optional;
import java.util.Properties;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.setDefaultMessageSender;
import static io.jexxa.common.drivenadapter.outbox.TransactionalOutboxProperties.outboxTable;
import static io.jexxa.common.drivenadapter.persistence.ObjectStoreFactory.setDefaultObjectStore;
import static io.jexxa.common.drivenadapter.persistence.RepositoryFactory.setDefaultRepository;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;


/**
 * This class supports unit testing of your application core, at least if you use infrastructure strategies
 * provided by Jexxa. To do so, this class performs the following steps:
 * <ul>
 * <li> Configuring an IMDB database for repositories </li>
 * <li> Configuring a message recorder for sending messages </li>
 * </ul>
 * An example of how to use this class can be found in tutorial <a href="https://github.com/jexxa-projects/Jexxa/tree/master/tutorials/BookStore">Bookstore</a>
 *
 */
public class JexxaTest
{
    public static final String JEXXA_TEST_PROPERTIES = "/jexxa-test.properties";

    private static JexxaMain jexxaMain;
    private static boolean reinitJexxaMain = false;

    private JexxaTest()
    {
        jexxaMain.addProperties( loadJexxaTestProperties() );

        initForUnitTests();
    }

    public static synchronized <T> JexxaTest getJexxaTest(Class<T> jexxaApplication)
    {
        if (jexxaMain == null || reinitJexxaMain) {
            jexxaMain = new JexxaMain(jexxaApplication);
        }
        return new JexxaTest();
    }


    @CheckReturnValue
    public <T> T getRepository(Class<T> repository)
    {
        if (!repository.isInterface())
        {
            throw new IllegalArgumentException("Given attribute of getRepository must be an interface");
        }
        return jexxaMain.getInstanceOfPort(repository);
    }

    @CheckReturnValue
    public <T> T getInstanceOfPort(Class<T> inboundPort)
    {
        return jexxaMain.getInstanceOfPort(inboundPort);
    }

    @CheckReturnValue
    public <T> MessageRecorder getMessageRecorder(Class<T> outboundPort)
    {
        var realImplementation = jexxaMain.getInstanceOfPort(outboundPort);
        return  MessageRecorderManager.getMessageRecorder(realImplementation.getClass());
    }

    public JexxaMain getJexxaMain()
    {
        return jexxaMain;
    }

    @SuppressWarnings({"unused","java:S2696"} )
    public <T> void registerStub(Class<T> outboundPort)
    {
        jexxaMain.registerDrivenAdapter(outboundPort);
        reinitJexxaMain = true;
    }

    @CheckReturnValue
    public Properties getProperties()
    {
        return getJexxaMain().getProperties();
    }

    private void initForUnitTests( )
    {
        setDefaultRepository(IMDBRepository.class);
        setDefaultObjectStore(IMDBObjectStore.class);
        setDefaultMessageSender(MessageRecordingStrategy.class);

        IMDBRepository.clear(); //Note: This clears IMDBRepository and IMDBObjectStore because IMDBObjectStore extends IMDBRepository and uses its internal data structure
        MessageRecorderManager.clear();
    }

    static Properties loadJexxaTestProperties()
    {
        var properties = new Properties();
        Optional.ofNullable(JexxaMain.class.getResourceAsStream(JEXXA_TEST_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load, getLogger(JexxaTest.class)),
                        () -> getLogger(JexxaTest.class).warn("Properties file '{}' not found", JEXXA_TEST_PROPERTIES)
                );
        if (!properties.containsKey(outboxTable())) {
            properties.put(outboxTable(),"jexxaoutboxmessage_test");
        }

        return properties;
    }
}
