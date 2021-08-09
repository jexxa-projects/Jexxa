package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.MetadataConverter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.NumericConverter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.StringConverter;

public class IMDBObjectStore<T, K, M extends Enum<M> & MetadataConverter>  extends IMDBRepository<T, K> implements IObjectStore<T, K, M>
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
    public <S> INumericQuery<T, S> getNumericQuery(M metadata, Class<S> queryType)
    {
        if ( !comparatorFunctions.contains(metadata) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        //noinspection unchecked
        NumericConverter<T, S> numberComparator = (NumericConverter) metadata.getValueConverter();

        return new IMDBNumericQuery<>(getOwnAggregateMap(), numberComparator, queryType);
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metadata, Class<S> queryType)
    {
        if ( !comparatorFunctions.contains(metadata) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        //noinspection unchecked
        StringConverter<T, S> stringComparator = (StringConverter) metadata.getValueConverter();

        return new IMDBStringQuery<>(getOwnAggregateMap(), stringComparator, queryType);
    }

}

