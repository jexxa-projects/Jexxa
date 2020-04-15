package io.ddd.jexxa.application.applicationservice;

import io.ddd.jexxa.application.domainservice.IJexxaAggregateRepository;

public class JexxaApplicationService
{
    final IJexxaAggregateRepository jexxaAggregateRepository;

    public JexxaApplicationService(IJexxaAggregateRepository jexxaAggregateRepository)
    {
        this.jexxaAggregateRepository = jexxaAggregateRepository;
    }

    public int getAggregateCount()
    {
        return jexxaAggregateRepository.get().size();
    }
}
