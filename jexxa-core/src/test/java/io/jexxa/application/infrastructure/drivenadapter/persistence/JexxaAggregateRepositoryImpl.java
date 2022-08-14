package io.jexxa.application.infrastructure.drivenadapter.persistence;

import io.jexxa.application.domain.model.JexxaAggregate;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.application.domain.model.JexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@SuppressWarnings({"unsused"})
public final class JexxaAggregateRepositoryImpl implements JexxaAggregateRepository
{

    private final IRepository<JexxaAggregate, JexxaValueObject> repository;

    private JexxaAggregateRepositoryImpl(IRepository<JexxaAggregate, JexxaValueObject> repository)
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

    public static JexxaAggregateRepository create(Properties properties)
    {
        return new JexxaAggregateRepositoryImpl((RepositoryManager.getRepository(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties)
        ));
    }
}
