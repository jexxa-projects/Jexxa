package io.jexxa.sample.simpletimeservice.domainservice;

import java.time.LocalTime;

public interface ITimePublisher
{
    void publish(LocalTime localTime);
}
