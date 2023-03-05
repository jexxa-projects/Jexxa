package io.jexxa.testapplication.domain.model;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface JexxaAggregateRepository
{
    void add(JexxaAggregate jexxaEntity);
    JexxaAggregate get(JexxaValueObject aggregateID);
    Optional<JexxaAggregate> find(JexxaValueObject aggregateID);
    List<JexxaAggregate> get();
    void update(JexxaAggregate aggregate);
    void remove(JexxaAggregate aggregate);
    void removeAll();
}
