package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

/**
 * A comparator provides a strategy to compare a specific element of an aggregate.
 *
 * Note: The specific value must be convertible to a number
 *
 * @param <T> Defines the type of the aggregate
 * @param <S> Defines the type of the value inside the aggregate
 */
public class Comparator<T, S>
{
    private final Function<T, S> valueAccessor;
    private final Function<S, ? extends Number> valueConverter;

    /**
     * Creates an Comparator object
     *
     * @param valueAccessor defines a function to access a specific value of the aggregate
     * @param valueConverter defines a function that converts a searched value into a Number for comparison
     */
    public Comparator(Function<T, S> valueAccessor,
                      Function<S, ? extends Number> valueConverter)
    {
        this.valueAccessor = Objects.requireNonNull( valueAccessor );
        this.valueConverter = Objects.requireNonNull( valueConverter );
    }

    /**
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param aggregate which provides the aggregate including the value that should be converted
     * @return Number representing the value
     */
    public Number convertAggregate(T aggregate)
    {
        Objects.requireNonNull(aggregate);
        return valueConverter.apply(valueAccessor.apply(aggregate));
    }

    /**
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param value which provides the value that should be converted
     * @return Number representing the value
     */
    public Number convertValue(S value)
    {
        Objects.requireNonNull(value);
        return valueConverter.apply(value);
    }

    /**
     * Compares the value of the aggregate with given value
     *
     * @param aggregate first aggregate
     * @param value that should be compared
     * @return 0 If the value aggregate is equal to given value
     *     -1 if value of aggregate1 < given value
     *     1 if value of aggregate1 > value
     */
    public int compareToValue(T aggregate, S value)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(value);

        var aggregateValue = new BigDecimal( convertAggregate(aggregate).toString() );
        var givenValue = new BigDecimal( convertValue(value).toString());

        return aggregateValue.compareTo(givenValue);
    }

    /**
     * Compares the value of the two aggregates which each other
     *
     * @param aggregate1 first aggregate
     * @param aggregate2 second aggregate
     * @return 0 If the value of aggregate1 is equal to value aggregate2
     *     -1 if value of aggregate1 < value of aggregate2
     *     1 if value of aggregate1 > value of aggregate2
     */
    public int compareToAggregate(T aggregate1, T aggregate2)
    {
        Objects.requireNonNull(aggregate1);
        Objects.requireNonNull(aggregate2);

        var aggregateValue1 = new BigDecimal( convertAggregate(aggregate1).toString() );
        var aggregateValue2 = new BigDecimal( convertAggregate(aggregate2).toString() );
        return aggregateValue1.compareTo(aggregateValue2);
    }


}

