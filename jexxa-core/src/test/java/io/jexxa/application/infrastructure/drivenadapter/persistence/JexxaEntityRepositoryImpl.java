package io.jexxa.application.infrastructure.drivenadapter.persistence;

import io.jexxa.application.domain.model.JexxaEntity;
import io.jexxa.application.domain.model.JexxaEntityRepository;
import io.jexxa.application.domain.model.JexxaValueObject;

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
