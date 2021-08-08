package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator;

import java.util.Objects;
import java.util.function.Function;

public abstract class Comparator<T, S, V>
{
    Function<T, S> valueAccessor;
    Function<S, ? extends V> valueConverter;

    Comparator(Function<T, S> valueAccessor,
               Function<S, ? extends V> valueConverter)
    {
        this.valueAccessor = Objects.requireNonNull( valueAccessor );
        this.valueConverter = Objects.requireNonNull( valueConverter );
    }

    /**
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param aggregate which provides the aggregate including the value that should be converted
     * @return Number representing the value
     */
    public V convertAggregate(T aggregate)
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
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param value which provides the value that should be converted
     * @return Number representing the value
     */
    public V convertValue(S value)
    {
        if ( value == null ) {
            return null;
        }
        return valueConverter.apply(value);
    }

    public abstract Class<V> getValueType();
}
