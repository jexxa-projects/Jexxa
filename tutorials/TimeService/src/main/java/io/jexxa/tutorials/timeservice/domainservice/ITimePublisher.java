package io.jexxa.tutorials.timeservice.domainservice;

import java.time.LocalTime;

public interface ITimePublisher
{
    void publish(LocalTime localTime);
}
