package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface IRepositoryConnection<T, K>
{
    @SuppressWarnings("EmptyMethod")
    void update(T aggregate);

    void remove(T aggregate);

    void add(T aggregate);

    Optional<T> get(K primaryKey);

    List<T> get();

}
