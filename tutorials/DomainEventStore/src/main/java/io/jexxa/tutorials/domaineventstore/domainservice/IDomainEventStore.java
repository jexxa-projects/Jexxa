package io.jexxa.tutorials.domaineventstore.domainservice;

import java.time.Instant;
import java.util.List;

import io.jexxa.tutorials.domaineventstore.domain.domainevent.MyDomainEvent;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.BatchNumber;

public interface IDomainEventStore
{
    void add(MyDomainEvent domainEvent);

    List<MyDomainEvent> get(Instant startTime, Instant endTime);

    List<MyDomainEvent> getBatchNumbersLessThan(BatchNumber batchNumber);

    List<MyDomainEvent> getLatestEvents(int number);
}
