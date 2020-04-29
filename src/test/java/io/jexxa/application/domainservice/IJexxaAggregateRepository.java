package io.jexxa.application.domainservice;

import java.util.List;
import java.util.Optional;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

public interface IJexxaAggregateRepository
{
    void add(JexxaAggregate jexxaAggregate);
    JexxaAggregate get(JexxaValueObject aggregateID);
    Optional<JexxaAggregate> find( JexxaValueObject aggregateID);
    List<JexxaAggregate> get();
    void update(JexxaAggregate aggregate);
    void remove(JexxaAggregate aggregate);
    void removeAll();
}
