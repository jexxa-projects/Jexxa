package io.jexxa.application.infrastructure.drivenadapter.persistence;

import io.jexxa.application.domain.model.JexxaAggregate;
import io.jexxa.application.domain.model.JexxaAggregateRepository;
import io.jexxa.application.domain.model.JexxaValueObject;

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
