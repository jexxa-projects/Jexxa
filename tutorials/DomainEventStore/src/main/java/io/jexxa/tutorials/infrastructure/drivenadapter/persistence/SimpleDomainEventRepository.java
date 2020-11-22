package io.jexxa.tutorials.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.ISimpleDomainEventRepository;

public class SimpleDomainEventRepository implements ISimpleDomainEventRepository
{
    private final IRepository<DomainEvent, String> repository;

    public SimpleDomainEventRepository(Properties properties)
    {
        repository = RepositoryManager.getRepository(DomainEvent.class, DomainEvent::getUUID, properties);
    }

    @Override
    public void add(DomainEvent domainEvent)
    {
        repository.add(domainEvent);
    }

    @Override
    public boolean isPresent(String uuid)
    {
        return repository.get(uuid).isPresent();
    }

    @Override
    public DomainEvent get(String uuid)
    {
        return repository.get(uuid).orElseThrow();
    }

    @Override
    public List<DomainEvent> getAll()
    {
        return repository.get();
    }
}
