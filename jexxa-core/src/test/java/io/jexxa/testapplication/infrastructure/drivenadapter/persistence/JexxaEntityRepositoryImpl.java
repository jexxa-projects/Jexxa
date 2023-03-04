package io.jexxa.testapplication.infrastructure.drivenadapter.persistence;

import io.jexxa.testapplication.domain.model.JexxaEntity;
import io.jexxa.testapplication.domain.model.JexxaEntityRepository;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import java.util.Properties;

public final class JexxaEntityRepositoryImpl
        extends GenericRepositoryImpl<JexxaEntity, JexxaValueObject>
        implements JexxaEntityRepository
{
    public JexxaEntityRepositoryImpl(Properties properties)
    {
        super(JexxaEntity.class, JexxaEntity::getKey, properties);
    }
}
