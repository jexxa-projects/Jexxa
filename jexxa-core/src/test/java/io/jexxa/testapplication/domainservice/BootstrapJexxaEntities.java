package io.jexxa.testapplication.domainservice;

import io.jexxa.testapplication.domain.model.JexxaEntity;
import io.jexxa.testapplication.domain.model.JexxaEntityRepository;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import java.util.stream.IntStream;

public record BootstrapJexxaEntities(JexxaEntityRepository jexxaEntityRepository) {

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
