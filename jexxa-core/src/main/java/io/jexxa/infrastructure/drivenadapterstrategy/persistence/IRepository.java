package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Interface for all strategies for the implementation of a repository in the terms of DDD
 *
 * A violation of preconditions is handled by an IllegalArgumentException.
 *
 * @param <T> Type of the aggregate
 * @param <K> Type of the aggregate ID
 *
 * @pre General precondition for all methods is that given attributes must not be <code>null</code>
 */
public interface IRepository<T, K>
{
    /**
     * Updates the given aggregate inside the repository.
     * @param aggregate that should be updated
     * @pre Given aggregate must be added by using {@link #add(Object)}}
     */
    void update(T aggregate);

    /**
     * Removed aggregate identified by given key.
     * @param key to the aggregate to be removed
     * @pre Aggregate must be added by using {@link #add(Object)}}
     */
    void remove(K key);

    /**
     * Removes all aggregates managed by the Repository
     */
    void removeAll();

    /**
     * Adds an aggregate to this repository
     * @param aggregate that should be added
     * @pre Aggregate must not be added before
     */
    void add(T aggregate);

    /**
     * Returns the aggregate identified by given key.
     * @param key that identifies the aggregate
     * @return Optional of aggregate. Optional is empty if no aggregate is found by given key.
     */
    Optional<T> get(K key);

    /**
     * Returns all aggregates managed by this repository
     * @return list of aggregates
     */
    List<T> get();
}
