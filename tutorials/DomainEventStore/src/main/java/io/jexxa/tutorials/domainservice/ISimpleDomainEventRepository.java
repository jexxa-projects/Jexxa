package io.jexxa.tutorials.domainservice;

import java.util.List;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;

public interface ISimpleDomainEventRepository
{
    void add(DomainEvent domainEvent);

    DomainEvent get(String uuid);

    List<DomainEvent> getAll();
}
