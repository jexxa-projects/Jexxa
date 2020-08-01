package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, K>
{
    void update(T aggregate);

    void remove(K key);

    void removeAll();

    void add(T aggregate);

    Optional<T> get(K primaryKey);

    List<T> get();
}
