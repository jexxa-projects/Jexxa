package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * A comparator provides a strategy to compare a specific element of an aggregate.
 *
 * Note: The specific value must be convertible to a number
 *
 * @param <T> Defines the type of the aggregate
 * @param <S> Defines the type of the value inside the aggregate
 */
public class NumericComparator<T, S>  extends Comparator<T, S, Number>
{
    /**
     * Creates an Comparator object
     *
     * @param valueAccessor defines a function to access a specific value of the aggregate
     * @param valueConverter defines a function that converts a searched value into a Number for comparison
     */
    public NumericComparator(Function<T, S> valueAccessor,
                                     Function<S, ? extends Number> valueConverter)
    {
        super(valueAccessor, valueConverter);
    }

    @Override
    protected int typeSpecificCompareTo(Number value1, Number value2)
    {
        //Handle both != null
        var aggregateValue1BD = new BigDecimal( value1.toString() );
        var aggregateValue2BD = new BigDecimal( value2.toString() );
        return aggregateValue1BD.compareTo(aggregateValue2BD);
    }
}

