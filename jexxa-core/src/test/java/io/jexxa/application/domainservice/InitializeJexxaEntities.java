package io.jexxa.application.domainservice;

import io.jexxa.application.domain.aggregate.JexxaEntity;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

import java.util.stream.IntStream;

public record InitializeJexxaEntities(IJexxaEntityRepository jexxaEntityRepository) {

    public void initDomainData() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .forEach(element -> addIfNotAvailable(new JexxaValueObject(element)));
    }

    private void addIfNotAvailable(JexxaValueObject aggregateID) {
        if (jexxaEntityRepository.find(aggregateID).isEmpty()) {
            jexxaEntityRepository.add(JexxaEntity.create(aggregateID));
        }
    }
}
