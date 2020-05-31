package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Throws IllegalArgumentException if any operations fails
 */
@SuppressWarnings("unused")
public interface IRepository<T, K>
{
    @SuppressWarnings("EmptyMethod")
    void update(T aggregate);

    void remove(K key);

    void removeAll();

    void add(T aggregate);

    Optional<T> get(K primaryKey);

    List<T> get();
}
