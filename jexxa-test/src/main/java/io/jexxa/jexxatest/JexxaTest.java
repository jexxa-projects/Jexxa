package io.jexxa.jexxatest;

import io.jexxa.common.annotation.CheckReturnValue;
import io.jexxa.common.function.ThrowingConsumer;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.MessageSenderManager;
import io.jexxa.infrastructure.ObjectStoreManager;
import io.jexxa.infrastructure.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.infrastructure.RepositoryManager;
import io.jexxa.infrastructure.persistence.repository.imdb.IMDBRepository;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecorder;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecorderManager;
import io.jexxa.jexxatest.infrastructure.messaging.recording.MessageRecordingStrategy;

import java.util.Optional;
import java.util.Properties;

import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;

/**
 * This class supports unit testing of your application core, at least if you use infrastructure strategies
 * provided by Jexxa. To do so, this class performs following steps:
 * <ul>
 * <li> Configuring an IMDB database for repositories </li>
 * <li> Configuring a message recorder for sending messages </li>
 * </ul>
 * An example how to use this class can be found in tutorial <a href="https://github.com/jexxa-projects/Jexxa/tree/master/tutorials/BookStore">Bookstore</a>
 *
 */
public class JexxaTest
{
    public static final String JEXXA_TEST_PROPERTIES = "/jexxa-test.properties";

    private static JexxaMain jexxaMain;

    private JexxaTest()
    {
        jexxaMain.addProperties( loadJexxaTestProperties() );

        initForUnitTests();
    }

    public static synchronized <T> JexxaTest getJexxaTest(Class<T> jexxaApplication)
    {
        if (jexxaMain == null) {
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

    @SuppressWarnings("unused")
    public <T> void registerStub(Class<T> outboundPort)
    {
        jexxaMain.registerDrivenAdapter(outboundPort);
    }

    @CheckReturnValue
    public Properties getProperties()
    {
        return getJexxaMain().getProperties();
    }

    private void initForUnitTests( )
    {
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        ObjectStoreManager.setDefaultStrategy(IMDBObjectStore.class);
        MessageSenderManager.setDefaultStrategy(MessageRecordingStrategy.class);

        IMDBRepository.clear();
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
        return properties;
    }
}
