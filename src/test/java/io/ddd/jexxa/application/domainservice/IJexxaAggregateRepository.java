package io.ddd.jexxa.application.domainservice;

import java.util.List;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;

public interface IJexxaAggregateRepository
{
    void add(JexxaAggregate jexxaAggregate);
    JexxaAggregate get(JexxaValueObject aggregateID);
    List<JexxaAggregate> get();
    void update(JexxaAggregate aggregate);
    void remove(JexxaAggregate aggregate);
}
