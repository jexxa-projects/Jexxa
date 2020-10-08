package io.jexxa.jexxatest;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorder;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorderManager;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecordingStrategy;
import org.apache.commons.lang3.Validate;

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
    private final JexxaMain jexxaMain;

    public JexxaTest(JexxaMain jexxaMain)
    {
        Validate.notNull(jexxaMain);
        this.jexxaMain = jexxaMain;

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
        T realImplementation = jexxaMain.getInstanceOfPort(outboundPort);
        return  MessageRecorderManager.getInstance().getMessageRecorder(realImplementation.getClass());
    }

    private void initForUnitTests( )
    {
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
        MessageSenderManager.getInstance().setDefaultStrategy(MessageRecordingStrategy.class);

        IMDBRepository.clear();
        MessageRecorderManager.clear();
    }
}
