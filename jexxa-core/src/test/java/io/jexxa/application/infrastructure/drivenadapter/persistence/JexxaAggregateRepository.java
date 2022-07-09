package io.jexxa.application.infrastructure.drivenadapter.persistence;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@SuppressWarnings({"unsused", "ClassCanBeRecord"})
public final class JexxaAggregateRepository implements IJexxaAggregateRepository
{

    private final IRepository<JexxaAggregate, JexxaValueObject> repository;

    private JexxaAggregateRepository(IRepository<JexxaAggregate, JexxaValueObject> repository)
    {
        this.repository = repository;
    }

    @Override
    public void add(JexxaAggregate jexxaEntity)
    {
        repository.add(jexxaEntity);
    }

    @Override
    public JexxaAggregate get(JexxaValueObject aggregateID)
    {
        return repository.get(aggregateID).orElseThrow();
    }

    @Override
    public Optional<JexxaAggregate> find(JexxaValueObject aggregateID)
    {
        return repository.get(aggregateID);
    }

    @Override
    public List<JexxaAggregate> get()
    {
        return repository.get();
    }

    @Override
    public void update(JexxaAggregate aggregate)
    {
        repository.update(aggregate);
    }

    @Override
    public void remove(JexxaAggregate aggregate)
    {
        repository.remove(aggregate.getKey());
    }

    @Override
    public void removeAll()
    {
        repository.removeAll();
    }

    public static IJexxaAggregateRepository create(Properties properties)
    {
        return new JexxaAggregateRepository((RepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties)
        ));
    }
}
