package io.jexxa.testapplication.applicationservice;

import io.jexxa.testapplication.domain.model.JexxaEntityRepository;

public class JexxaApplicationService
{
    private final JexxaEntityRepository jexxaAggregateRepository;

    public JexxaApplicationService(JexxaEntityRepository jexxaAggregateRepository)
    {
        this.jexxaAggregateRepository = jexxaAggregateRepository;
    }

    public int getAggregateCount()
    {
        return jexxaAggregateRepository.get().size();
    }
}
