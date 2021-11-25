package io.jexxa.jexxatest;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorder;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorderManager;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecordingStrategy;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * This class supports unit testing of your application core, at least if you use driven adapter strategies
 * provided by Jexxa. To do so, this class performs following steps:
 * <ul>
 * <li> Configuring an IMDB database for repositories </li>
 * <li> Configuring a message recorder for sending messages </li>
 * </ul>
 * An example how to use this class can be found in tutorial <a href="https://github.com/repplix/Jexxa/tree/master/tutorials/BookStore">Bookstore</a>
 *
 */
public class JexxaTest
{
    public static final String JEXXA_TEST_PROPERTIES = "/jexxa-test.properties";

    private final JexxaMain jexxaMain;

    public JexxaTest(JexxaMain jexxaMain)
    {
        Objects.requireNonNull(jexxaMain);
        this.jexxaMain = jexxaMain;
        this.jexxaMain.addProperties( loadProperties() );

        initForUnitTests();
    }

    public <T> T getRepository(Class<T> repository)
    {
        return jexxaMain.getInstanceOfPort(repository);
    }

    public <T> T getInstanceOfPort(Class<T> inboundPort)
    {
        return jexxaMain.getInstanceOfPort(inboundPort);
    }

    public <T> MessageRecorder getMessageRecorder(Class<T> outboundPort)
    {
        var realImplementation = jexxaMain.getInstanceOfPort(outboundPort);
        return  MessageRecorderManager.getMessageRecorder(realImplementation.getClass());
    }

    private void initForUnitTests( )
    {
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        ObjectStoreManager.setDefaultStrategy(IMDBObjectStore.class);
        MessageSenderManager.setDefaultStrategy(MessageRecordingStrategy.class);

        IMDBRepository.clear();
        MessageRecorderManager.clear();
    }

    private Properties loadProperties()
    {
        var properties = new Properties();
        Optional.ofNullable(JexxaMain.class.getResourceAsStream(JEXXA_TEST_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load),
                        () -> JexxaLogger.getLogger(this.getClass()).warn("NO PROPERTIES FILE FOUND {}", JEXXA_TEST_PROPERTIES)
                );
        return properties;
    }
}
