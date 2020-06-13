package io.jexxa.samples.simpletimeservice.domainservice;

import java.time.LocalTime;

public interface ITimePublisher
{
    void publish(LocalTime localTime);
}
