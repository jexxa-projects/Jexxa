package io.jexxa.application.infrastructure.drivenadapter.persistence;

import io.jexxa.application.domain.model.JexxaEntity;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.application.domain.model.JexxaEntityRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public final class JexxaEntityRepositoryImpl implements JexxaEntityRepository
{
    private final IRepository<JexxaEntity, JexxaValueObject> repositoryConnection;

    private JexxaEntityRepositoryImpl(IRepository<JexxaEntity, JexxaValueObject> repositoryConnection)
    {
        this.repositoryConnection = repositoryConnection;
    }

    @Override
    public void add(JexxaEntity jexxaEntity)
    {
        repositoryConnection.add(jexxaEntity);
    }

    @Override
    public JexxaEntity get(JexxaValueObject aggregateID)
    {
        return repositoryConnection.get(aggregateID).orElseThrow();
    }

    @Override
    public Optional<JexxaEntity> find(JexxaValueObject aggregateID)
    {
        return repositoryConnection.get(aggregateID);
    }

    @Override
    public List<JexxaEntity> get()
    {
        return repositoryConnection.get();
    }

    @Override
    public void update(JexxaEntity aggregate)
    {
        repositoryConnection.update(aggregate);
    }

    @Override
    public void remove(JexxaEntity aggregate)
    {
        repositoryConnection.remove(aggregate.getKey());
    }

    @Override
    public void removeAll()
    {
        repositoryConnection.removeAll();
    }

    public static JexxaEntityRepository create(Properties properties)
    {
        return new JexxaEntityRepositoryImpl(RepositoryManager.getRepository(
                JexxaEntity.class,
                JexxaEntity::getKey,
                properties)
        );
    }
}
