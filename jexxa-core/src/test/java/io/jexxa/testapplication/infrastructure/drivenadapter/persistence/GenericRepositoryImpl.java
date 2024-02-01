package io.jexxa.testapplication.infrastructure.drivenadapter.persistence;

import io.jexxa.common.drivenadapter.persistence.RepositoryManager;
import io.jexxa.common.drivenadapter.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Generic implementation of a Repository including typical methods
 *
 * @param <A> represents the type of the Aggregate
 * @param <I> represents the type of the AggregateID
 */
public class GenericRepositoryImpl<A, I>
{

    private final Function<A, I> keyFunction;
    private final IRepository<A, I> repository;

    public GenericRepositoryImpl(Class<A> aggregateClass,
                                 Function<A, I> keyFunction,
                                 Properties properties)
    {
        this.keyFunction = keyFunction;
        this.repository = RepositoryManager.getRepository(
                aggregateClass,
                keyFunction,
                properties);
    }

    public void add(A jexxaEntity)
    {
        repository.add(jexxaEntity);
    }

    public A get(I aggregateID)
    {
        return repository.get(aggregateID).orElseThrow();
    }

    @SuppressWarnings("unused")
    public Optional<A> find(I aggregateID)
    {
        return repository.get(aggregateID);
    }

    public List<A> get()
    {
        return repository.get();
    }

    public void update(A aggregate)
    {
        repository.update(aggregate);
    }

    public void remove(A aggregate)
    {
        repository.remove(keyFunction.apply(aggregate));
    }

    public void removeAll()
    {
        repository.removeAll();
    }
}
