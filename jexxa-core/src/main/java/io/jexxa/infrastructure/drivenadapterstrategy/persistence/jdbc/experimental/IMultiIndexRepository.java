package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

public interface IMultiIndexRepository<T, V, M  extends Enum<?> & ComparatorSchema> extends IRepository<T, V>
{
    <S > IQuery<T, S > getSubset(M strategy);
}
