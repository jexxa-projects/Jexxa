package io.jexxa.jexxatest;

import java.util.ArrayList;
import java.util.List;

public class DomainEventRecorder<T> {
    private final List<T> domainEvents = new ArrayList<>();

    public void receive(T domainEvent)
    {
        domainEvents.add(domainEvent);
    }

    public List<T> get()
    {
        return domainEvents;
    }
}