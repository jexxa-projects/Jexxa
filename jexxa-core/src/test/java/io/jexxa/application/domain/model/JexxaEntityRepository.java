package io.jexxa.application.domain.model;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface JexxaEntityRepository
{
    void add(JexxaEntity jexxaEntity);
    JexxaEntity get(JexxaValueObject aggregateID);
    Optional<JexxaEntity> find(JexxaValueObject aggregateID);
    List<JexxaEntity> get();
    void update(JexxaEntity aggregate);
    void remove(JexxaEntity aggregate);
    void removeAll();
}
