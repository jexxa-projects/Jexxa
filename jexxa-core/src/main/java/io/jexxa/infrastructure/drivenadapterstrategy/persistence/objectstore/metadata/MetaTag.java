package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata;

import java.util.Objects;
import java.util.function.Function;

public abstract class MetaTag<T, S, V>
{
    Function<T, S> valueAccessor;
    IConverter<S, ? extends V> valueIConverter;

    protected MetaTag(Function<T, S> valueAccessor,
                      IConverter<S, ? extends V> valueIConverter)
    {
        this.valueAccessor = Objects.requireNonNull( valueAccessor );
        this.valueIConverter = Objects.requireNonNull(valueIConverter);
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

        return valueIConverter.convert(value);
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
        return valueIConverter.convert(value);
    }

    public abstract Class<V> getValueType();
}
