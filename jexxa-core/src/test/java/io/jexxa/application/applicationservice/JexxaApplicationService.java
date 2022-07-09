package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.IJexxaEntityRepository;

@SuppressWarnings("ClassCanBeRecord")
public class JexxaApplicationService
{
    private final IJexxaEntityRepository jexxaAggregateRepository;

    public JexxaApplicationService(IJexxaEntityRepository jexxaAggregateRepository)
    {
        this.jexxaAggregateRepository = jexxaAggregateRepository;
    }

    public int getAggregateCount()
    {
        return jexxaAggregateRepository.get().size();
    }
}
