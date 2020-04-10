package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Optional;

public interface IDBConnection<T>
{
    void update(T aggregate);

    void remove(T aggregate);

    void add(T aggregate);

    <K> Optional<T> get(K primaryKey);

    List<T> get();

}
