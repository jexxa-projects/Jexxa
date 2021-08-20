package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class provides all information to perform queries with a concrete meta information.
 *
 * @param <T> Type of the managed aggregate/object
 * @param <S> Type of the metadata as it is used inside the aggregate/object
 * @param <V> Type that that is used by the meta tag to perform query operations
 */
public abstract class MetaTag<T, S, V>
{
    private final Function<T, S> valueAccessor;
    private final Function<S, ? extends V> valueConverter;

    protected MetaTag(Function<T, S> valueAccessor,
                      Function<S, ? extends V> valueConverter)
    {
        this.valueAccessor = Objects.requireNonNull( valueAccessor );
        this.valueConverter = Objects.requireNonNull(valueConverter);
    }

    /**
     * This method converts the value of type {@link S} stored in the aggregate {@link T} to type {@link V} by using the defined value converter
     * @param aggregate which provides the aggregate including the value that should be converted
     * @return {@link V} representing the value of the MetaTag
     */
    public V getFromAggregate(T aggregate)
    {
        Objects.requireNonNull(aggregate);
        var value = valueAccessor.apply(aggregate);

        if (value == null)
        {
            return null;
        }

        return valueConverter.apply(value);
    }

    /**
     * This method converts the value of type {@link S} stored in the aggregate to {@link V} by using the defined converter function
     * @param value which provides the value that should be converted
     * @return {@link V} representing the value
     */
    public V getFromValue(S value)
    {
        if ( value == null ) {
            return null;
        }
        return valueConverter.apply(value);
    }

    /**
     * Returns the concrete type information of the tag that is used for performing all queries
     *
     * @return concrete type information of the tag that is used for performing all queries
     */
    public abstract Class<V> getTagType();
}
