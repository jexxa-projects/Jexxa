package io.jexxa.testapplication.infrastructure.drivenadapter.persistence;

import io.jexxa.testapplication.domain.model.JexxaAggregate;
import io.jexxa.testapplication.domain.model.JexxaAggregateRepository;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import java.util.Properties;

@SuppressWarnings({"unsused"})
public final class JexxaAggregateRepositoryImpl
        extends GenericRepositoryImpl<JexxaAggregate, JexxaValueObject>
        implements JexxaAggregateRepository
{
    public JexxaAggregateRepositoryImpl(Properties properties)
    {
        super(JexxaAggregate.class, JexxaAggregate::getKey, properties);
    }
}
