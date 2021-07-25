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

    /**
     * Compares the value of the aggregate with given value
     *
     * @param aggregate first aggregate
     * @param value that should be compared
     * @return 0 If the value aggregate is equal to given value <br>
     *     -1 if value of aggregate &lt; given value <br>
     *     1 if value of aggregate &gt; value <br>
     */
    public int compareToValue(T aggregate, S value)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(value);

        if(convertAggregate(aggregate) == null)
        {
            return 1;
        }

        var aggregateValue = convertAggregate(aggregate);

        return typeSpecificCompareTo(aggregateValue, convertValue(value));
    }

    /**
     * Compares the value of the two aggregates which each other
     *
     * @param aggregate1 first aggregate
     * @param aggregate2 second aggregate
     * @return 0 If the value of aggregate1 is equal to value aggregate2 <br>
     *     -1 if value of aggregate1 &lt; value of aggregate2 <br>
     *     1 if value of aggregate1 &gt; value of aggregate2 <br>
     */
    public int compareToAggregate(T aggregate1, T aggregate2)
    {
        Objects.requireNonNull(aggregate1);
        Objects.requireNonNull(aggregate2);

        var aggregateValue1 = convertAggregate(aggregate1);
        var aggregateValue2 = convertAggregate(aggregate2);

        if ( aggregateValue1 == null && aggregateValue2 == null)
        {
            return 0;
        } else if ( aggregateValue1 == null)
        {
            return 1;
        } else if ( aggregateValue2 == null)
        {
            return 1;
        }

        return typeSpecificCompareTo( aggregateValue1, aggregateValue2);
    }

    public abstract Class<V> getValueType();

    protected abstract int typeSpecificCompareTo(V value1, V value2);
}
