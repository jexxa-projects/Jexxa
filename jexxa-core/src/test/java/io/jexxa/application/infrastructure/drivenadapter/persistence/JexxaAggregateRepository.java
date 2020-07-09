package io.jexxa.application.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;

public final class JexxaAggregateRepository implements IJexxaAggregateRepository
{
    private final IRepository<JexxaAggregate, JexxaValueObject> repositoryConnection;

    private JexxaAggregateRepository(IRepository<JexxaAggregate, JexxaValueObject> repositoryConnection)
    {
        this.repositoryConnection = repositoryConnection;
    }

    @Override
    public void add(JexxaAggregate jexxaAggregate)
    {
        repositoryConnection.add(jexxaAggregate);
    }

    @Override
    public JexxaAggregate get(JexxaValueObject aggregateID)
    {
        return repositoryConnection.get(aggregateID).orElseThrow();
    }

    @Override
    public Optional<JexxaAggregate> find(JexxaValueObject aggregateID)
    {
        return repositoryConnection.get(aggregateID);
    }

    @Override
    public List<JexxaAggregate> get()
    {
        return repositoryConnection.get();
    }

    @Override
    public void update(JexxaAggregate aggregate)
    {
        repositoryConnection.update(aggregate);
    }

    @Override
    public void remove(JexxaAggregate aggregate)
    {
        repositoryConnection.remove(aggregate.getKey());
    }
    
    @Override
    public void removeAll()
    {
        repositoryConnection.removeAll();
    }

    public static IJexxaAggregateRepository create(Properties properties)
    {
        return new JexxaAggregateRepository(RepositoryManager.getInstance().getStrategy(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties)
        );
    }
}
