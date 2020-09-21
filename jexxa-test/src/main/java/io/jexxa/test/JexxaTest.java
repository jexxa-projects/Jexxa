package io.jexxa.test;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
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

    private void initForUnitTests( )
    {
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
    }
}
