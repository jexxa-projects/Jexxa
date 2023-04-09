package io.jexxa.testapplication.infrastructure.drivenadapter.persistence;

import io.jexxa.infrastructure.RepositoryManager;
import io.jexxa.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class GenericRepositoryImpl<Aggregate, AggregateID>
{

    private final Function<Aggregate, AggregateID> keyFunction;
    private final IRepository<Aggregate, AggregateID> repository;

    public GenericRepositoryImpl(Class<Aggregate> aggregateClass,
                                 Function<Aggregate, AggregateID> keyFunction,
                                 Properties properties)
    {
        this.keyFunction = keyFunction;
        this.repository = RepositoryManager.getRepository(
                aggregateClass,
                keyFunction,
                properties);
    }

    public void add(Aggregate jexxaEntity)
    {
        repository.add(jexxaEntity);
    }

    public Aggregate get(AggregateID aggregateID)
    {
        return repository.get(aggregateID).orElseThrow();
    }

    @SuppressWarnings("unused")
    public Optional<Aggregate> find(AggregateID aggregateID)
    {
        return repository.get(aggregateID);
    }

    public List<Aggregate> get()
    {
        return repository.get();
    }

    public void update(Aggregate aggregate)
    {
        repository.update(aggregate);
    }

    public void remove(Aggregate aggregate)
    {
        repository.remove(keyFunction.apply(aggregate));
    }

    public void removeAll()
    {
        repository.removeAll();
    }
}
