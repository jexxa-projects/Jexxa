package io.jexxa.samples.simpletimeservice.applicationservice;

import java.time.LocalTime;

import io.jexxa.samples.simpletimeservice.domainservice.ITimePublisher;

public class TimeService
{
    private final ITimePublisher timePublisher;

    public TimeService(ITimePublisher timePublisher)
    {
        this.timePublisher = timePublisher;
    }

    public LocalTime getTime()
    {
        return LocalTime.now();
    }

    @SuppressWarnings("unused")
    public void publishTime()
    {
        timePublisher.publish(getTime());
    }
}
