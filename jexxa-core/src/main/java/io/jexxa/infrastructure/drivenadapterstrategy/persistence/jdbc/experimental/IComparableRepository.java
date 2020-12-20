package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

public interface IComparableRepository<T, V, M  extends Enum<?> & Strategy> extends IRepository<T, V>
{
    <S > IRangedResult<T, S > getRangeInterface(M strategy);

}
