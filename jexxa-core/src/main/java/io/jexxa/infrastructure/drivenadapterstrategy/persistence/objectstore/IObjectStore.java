package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;

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
     * This method returns an INumericQuery that can be used to search for elements of type S
     * managed by the aggregate.
     *
     * @param metadata defines metadata-comparator used for searching objects
     * @param queryType defines the type of the metadata used for searching inside the {@link IObjectStore}
     * @return interface to execute the query
     */
    <S > INumericQuery<V, S > getNumericQuery(R metadata, Class<S> queryType);


    /**
     * This method returns an IStringQuery hat can be used to search for elements of type S
     * managed by the aggregate.
     *
     * @param metadata defines metadata-comparator used for searching objects
     * @param queryType defines the type of the metadata used for searching inside the {@link IObjectStore}
     * @return interface to execute the query
     */
    <S > IStringQuery<V, S > getStringQuery(R metadata, Class<S> queryType);
}