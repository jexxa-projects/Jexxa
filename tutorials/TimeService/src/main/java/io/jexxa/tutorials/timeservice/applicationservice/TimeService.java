package io.jexxa.tutorials.timeservice.applicationservice;

import java.time.LocalTime;

import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public class TimeService
{
    private final ITimePublisher timePublisher;

    /**
     * Note: Jexxa supports only implicit constructor injection. Therefore, we must
     * declare all required interfaces in the constructor.  
     *
     * @param timePublisher required outbound port for this application service
     */
    public TimeService(ITimePublisher timePublisher)
    {
        this.timePublisher = timePublisher;
    }

    public LocalTime getTime()
    {
        return LocalTime.now();
    }

    public void publishTime()
    {
        timePublisher.publish(getTime());
    }

    public void timePublished(LocalTime localTime)
    {
        JexxaLogger.getLogger(TimeService.class).info("New Time was published time {} ", localTime.toString());
    }

}
