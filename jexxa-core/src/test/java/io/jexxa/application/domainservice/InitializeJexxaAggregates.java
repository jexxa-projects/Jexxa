package io.jexxa.application.domainservice;

import io.jexxa.application.domain.model.JexxaAggregate;
import io.jexxa.application.domain.model.JexxaAggregateRepository;
import io.jexxa.application.domain.model.JexxaValueObject;

import java.util.stream.IntStream;

public record InitializeJexxaAggregates(JexxaAggregateRepository jexxaAggregateRepository) {

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
