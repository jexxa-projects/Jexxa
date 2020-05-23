package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.IJexxaAggregateRepository;

public class JexxaApplicationService
{
    private final IJexxaAggregateRepository jexxaAggregateRepository;

    public JexxaApplicationService(IJexxaAggregateRepository jexxaAggregateRepository)
    {
        this.jexxaAggregateRepository = jexxaAggregateRepository;
    }

    public int getAggregateCount()
    {
        return jexxaAggregateRepository.get().size();
    }
}
