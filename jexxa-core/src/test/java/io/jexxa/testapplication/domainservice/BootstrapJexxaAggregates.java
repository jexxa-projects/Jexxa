package io.jexxa.testapplication.domainservice;

import io.jexxa.testapplication.domain.model.JexxaAggregate;
import io.jexxa.testapplication.domain.model.JexxaAggregateRepository;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import java.util.stream.IntStream;

public record BootstrapJexxaAggregates(JexxaAggregateRepository jexxaAggregateRepository) {

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
