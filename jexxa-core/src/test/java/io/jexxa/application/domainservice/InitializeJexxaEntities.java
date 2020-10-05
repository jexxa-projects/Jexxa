package io.jexxa.application.domainservice;

import java.util.stream.IntStream;

import io.jexxa.application.domain.aggregate.JexxaEntity;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

public class InitializeJexxaEntities
{
    private final IJexxaEntityRepository jexxaEntityRepository;

    public InitializeJexxaEntities(IJexxaEntityRepository jexxaEntityRepository)
    {
        this.jexxaEntityRepository = jexxaEntityRepository;
    }

    public void initDomainData()
    {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .forEach(element -> addIfNotAvailable(new JexxaValueObject(element)));
    }

    private void addIfNotAvailable(JexxaValueObject aggregateID) {
        if (jexxaEntityRepository.find(aggregateID).isEmpty())
        {
            jexxaEntityRepository.add(JexxaEntity.create(aggregateID));
        }
    }
}
