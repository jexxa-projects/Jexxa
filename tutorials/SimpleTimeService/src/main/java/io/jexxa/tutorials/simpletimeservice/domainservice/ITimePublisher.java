package io.jexxa.tutorials.simpletimeservice.domainservice;

import java.time.LocalTime;

public interface ITimePublisher
{
    void publish(LocalTime localTime);
}
