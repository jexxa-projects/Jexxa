package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

/**
 * An {@link IObjectStore} extends an {@link IRepository} by adding additional metadata to managed objects.
 * This meta data can be used to query for available objects.
 *
 * In contrast to an {@link IRepository} this interface allows for using sophisticated
 * optimization techniques from the underlying technology stack to perform the query
 *
 * @param <V> Defines the type of the object managed by the repository
 * @param <K> Defines the type of the key identifying the managed object
 * @param <R> Defines the type of the Schema of the aggregate which defines
 *           an enum for each searchable value of the aggregate
 */
public interface IObjectStore<V, K, R extends Enum<?> & MetadataComparator> extends IRepository<V, K>
{
    /**
     * This method returns an IQuery object hat can be used to search for elements of type S
     * managed by the aggregate.
     *
     * @param schemaEntry defines comparator strategy used for searching entries
     * @param <S> defines the type of the searched element which is stored inside the
     * @return interface to execute the query
     */
    <S > IQuery<V, S > getIQuery(R schemaEntry);
}
