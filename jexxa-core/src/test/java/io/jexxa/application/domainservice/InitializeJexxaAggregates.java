package io.jexxa.application.domainservice;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

import java.util.stream.IntStream;

public record InitializeJexxaAggregates(IJexxaAggregateRepository jexxaAggregateRepository) {

    public void initDomainData() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .forEach(element -> addIfNotAvailable(new JexxaValueObject(element)));
    }

    private void addIfNotAvailable(JexxaValueObject aggregateID) {
        if (jexxaAggregateRepository.find(aggregateID).isEmpty()) {
            jexxaAggregateRepository.add(JexxaAggregate.create(aggregateID));
        }
    }
}
