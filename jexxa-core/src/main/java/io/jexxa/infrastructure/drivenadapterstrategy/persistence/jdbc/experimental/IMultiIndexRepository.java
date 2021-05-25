package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

public interface IMultiIndexRepository<T, V, M  extends Enum<?> & SearchStrategy> extends IRepository<T, V>
{
    <S > ISubset<T, S > getSubset(M strategy);
}
