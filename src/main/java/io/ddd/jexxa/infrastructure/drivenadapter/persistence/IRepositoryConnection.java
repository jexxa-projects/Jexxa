package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Optional;

/**
 * TODO: Check if return value of get should be changed to Set&lt;T&gt;
 * Throws IllegalArgumentException if any operations fails   
 */
@SuppressWarnings("unused")
public interface IRepositoryConnection<T, K>
{
    @SuppressWarnings("EmptyMethod")
    void update(T aggregate);

    void remove(K key);

    void removeAll();

    void add(T aggregate);

    Optional<T> get(K primaryKey);

    List<T> get();


}
