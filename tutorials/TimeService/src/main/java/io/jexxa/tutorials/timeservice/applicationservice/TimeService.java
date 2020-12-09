package io.jexxa.tutorials.timeservice.applicationservice;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import io.jexxa.tutorials.timeservice.domainservice.IMessageDisplay;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;

@SuppressWarnings("unused")
public class TimeService
{
    private final ITimePublisher timePublisher;
    private final IMessageDisplay messageDisplay;

    /**
     * Note: Jexxa supports only implicit constructor injection. Therefore, we must
     * declare all required interfaces in the constructor.
     *
     * @param timePublisher required outbound port for this application service
     * @param messageDisplay required outbound port for this application service
     */
    public TimeService(ITimePublisher timePublisher, IMessageDisplay messageDisplay)
    {
        Objects.requireNonNull(timePublisher);
        Objects.requireNonNull(messageDisplay);

        this.timePublisher = timePublisher;
        this.messageDisplay = messageDisplay;
    }

    public LocalTime getTime()
    {
        return LocalTime.now();
    }

    public void publishTime()
    {
        timePublisher.publish(getTime());
    }


    /**
     * This method shows the previously published time.
     * @param localTime the previously published time
     */
    public void displayPublishedTime(LocalTime localTime)
    {
        var messageWithPublishedTime = "New Time was published, time: " + localTime.format(DateTimeFormatter.ISO_TIME);
        messageDisplay.show(messageWithPublishedTime);
    }
}
