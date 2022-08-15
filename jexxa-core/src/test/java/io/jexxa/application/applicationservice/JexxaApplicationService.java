package io.jexxa.application.applicationservice;

import io.jexxa.application.domain.model.JexxaEntityRepository;

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
