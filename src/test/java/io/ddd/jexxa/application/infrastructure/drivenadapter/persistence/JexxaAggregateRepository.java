package io.ddd.jexxa.application.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.IRepositoryConnection;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.RepositoryManager;

public class JexxaAggregateRepository implements IJexxaAggregateRepository
{
    private final IRepositoryConnection<JexxaAggregate, JexxaValueObject> repositoryConnection;

    private JexxaAggregateRepository(IRepositoryConnection<JexxaAggregate, JexxaValueObject> repositoryConnection)
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

    static public IJexxaAggregateRepository create(Properties properties)
    {
        return new JexxaAggregateRepository(RepositoryManager.getConnection(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties)
        );
    }
}
