package io.jexxa.application.domainservice;

import java.util.List;
import java.util.Optional;

import io.jexxa.application.domain.aggregate.JexxaEntity;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

public interface IJexxaEntityRepository
{
    void add(JexxaEntity jexxaEntity);
    JexxaEntity get(JexxaValueObject aggregateID);
    Optional<JexxaEntity> find(JexxaValueObject aggregateID);
    List<JexxaEntity> get();
    void update(JexxaEntity aggregate);
    void remove(JexxaEntity aggregate);
    void removeAll();
}
