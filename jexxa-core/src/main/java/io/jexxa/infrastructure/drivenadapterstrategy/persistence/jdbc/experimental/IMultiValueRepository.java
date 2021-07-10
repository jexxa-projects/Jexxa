package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

/**
 * This kind of repository allows to query the managed objects by an arbitrary number of values.
 * In contrast to this an {@link IRepository} allows only to query objects, or in more
 * detail aggregates, by its keys. Especially for aggregates in terms of DDD this is sufficient.
 *
 * But in some cases you need advanced query mechanisms which also allow using sophisticated
 * optimization techniques from the underlying technology stack. In these cases, this type of repository
 * should be used.
 *
 * @param <V> Defines the type of the object managed by the repository
 * @param <K> Defines the type of the key identifying the managed object
 * @param <R> Defines the type of the Schema of the aggregate which defines
 *           an enum for each searchable value of the aggregate
 */
public interface IMultiValueRepository<V, K, R extends Enum<?> & SchemaComparator> extends IRepository<V, K>
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
