package io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.display;

import io.jexxa.tutorials.timeservice.domainservice.IMessageDisplay;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public class MessageDisplay implements IMessageDisplay
{
    @Override
    public void show(String message)
    {
        JexxaLogger.getLogger(MessageDisplay.class).info(message);
    }
}
