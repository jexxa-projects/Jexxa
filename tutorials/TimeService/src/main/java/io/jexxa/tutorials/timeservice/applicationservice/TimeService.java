package io.jexxa.tutorials.timeservice.applicationservice;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import io.jexxa.tutorials.timeservice.domainservice.IMessageDisplay;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;
import org.apache.commons.lang3.Validate;

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
        Validate.notNull(timePublisher);
        Validate.notNull(messageDisplay);

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
    public void displayPublishedTimed(LocalTime localTime)
    {
        var messageWithPublishedTime = "New Time was published, time: " + localTime.format(DateTimeFormatter.ISO_TIME);
        messageDisplay.show(messageWithPublishedTime);
    }

}
