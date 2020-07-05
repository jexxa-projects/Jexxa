package io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.console;

import java.time.LocalTime;

import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

public class ConsoleTimePublisher implements ITimePublisher
{

    private static final Logger LOGGER = JexxaLogger.getLogger(ConsoleTimePublisher.class);

    @Override
    public void publish(LocalTime localTime)
    {
        Validate.notNull(localTime);

        var logMessage = localTime.toString();

        LOGGER.info(logMessage);
    }
}
