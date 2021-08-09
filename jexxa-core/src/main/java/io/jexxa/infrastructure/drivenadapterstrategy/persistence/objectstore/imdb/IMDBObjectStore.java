package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.NumericTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.StringTag;

public class IMDBObjectStore<T, K, M extends Enum<M> & MetadataSchema>  extends IMDBRepository<T, K> implements IObjectStore<T, K, M>
{
    private final Set<M> comparatorFunctions;

    public IMDBObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> comparatorSchema,
            Properties properties
            )
    {
        super(aggregateClazz, keyFunction, properties);
        this.comparatorFunctions = EnumSet.allOf(comparatorSchema);
    }

    @Override
    public <S> INumericQuery<T, S> getNumericQuery(M metaTag, Class<S> queryType)
    {
        if ( !comparatorFunctions.contains(metaTag) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        //noinspection unchecked
        NumericTag<T, S> numericTag = (NumericTag) metaTag.getTag();

        return new IMDBNumericQuery<>(getOwnAggregateMap(), numericTag, queryType);
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metaTag, Class<S> queryType)
    {
        if ( !comparatorFunctions.contains(metaTag) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        //noinspection unchecked
        StringTag<T, S> stringTag = (StringTag) metaTag.getTag();

        return new IMDBStringQuery<>(getOwnAggregateMap(), stringTag, queryType);
    }

}

