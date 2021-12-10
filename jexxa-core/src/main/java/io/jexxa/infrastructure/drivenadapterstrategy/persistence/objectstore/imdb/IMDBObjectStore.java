package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.NumericTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.StringTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.imdb.IMDBRepository;

import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class IMDBObjectStore<T, K, M extends Enum<M> & MetadataSchema>  extends IMDBRepository<T, K> implements IObjectStore<T, K, M>
{
    private final Set<M> metaData;

    public IMDBObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> metaData,
            Properties properties
            )
    {
        super(aggregateClazz, keyFunction, properties);
        this.metaData = EnumSet.allOf(metaData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> INumericQuery<T, S> getNumericQuery(M metaTag, Class<S> queryType)
    {
        if ( !metaData.contains(metaTag) )
        {
            throw new IllegalArgumentException("Unknown strategy for "+ metaTag.name());
        }

        //noinspection unchecked
        NumericTag<T, S> numericTag = (NumericTag) metaTag.getTag();

        return new IMDBNumericQuery<>(getOwnAggregateMap(), numericTag, queryType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> IStringQuery<T, S> getStringQuery(M metaTag, Class<S> queryType)
    {
        if ( !metaData.contains(metaTag) )
        {
            throw new IllegalArgumentException("Unknown strategy for " + metaTag.name());
        }

        //noinspection unchecked
        StringTag<T, S> stringTag = (StringTag) metaTag.getTag();

        return new IMDBStringQuery<>(getOwnAggregateMap(), stringTag, queryType);
    }

}

