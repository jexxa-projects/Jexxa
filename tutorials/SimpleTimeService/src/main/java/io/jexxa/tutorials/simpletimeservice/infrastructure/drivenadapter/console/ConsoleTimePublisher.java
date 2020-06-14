package io.jexxa.tutorials.simpletimeservice.infrastructure.drivenadapter.console;

import java.time.LocalTime;

import io.jexxa.tutorials.simpletimeservice.domainservice.ITimePublisher;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

public class ConsoleTimePublisher implements ITimePublisher
{

    private static final Logger logger = JexxaLogger.getLogger(ConsoleTimePublisher.class);

    @Override
    public void publish(LocalTime localTime)
    {
        Validate.notNull(localTime);

        var logMessage = localTime.toString();

        logger.info(logMessage);
    }
}
