package io.jexxa.test;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.test.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorder;
import io.jexxa.test.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecordingStrategy;
import io.jexxa.test.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorderManager;
import org.apache.commons.lang3.Validate;

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
        var realImplementation = jexxaMain.getInstanceOfPort(outboundPort);
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
