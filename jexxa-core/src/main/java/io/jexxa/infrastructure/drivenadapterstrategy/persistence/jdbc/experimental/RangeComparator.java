package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.util.function.Function;

public class RangeComparator<T, S>
{
    private final Function<T, S> aggregateAccessor;
    private final Function<S, ? extends Number> converterFunction;

    public RangeComparator(Function<T, S> aggregateAccessor,
                           Function<S, ? extends Number> converterFunction)
    {
        this.aggregateAccessor = aggregateAccessor;
        this.converterFunction = converterFunction;
    }

    public Number convertAggregate(T aggregate)
    {
        return converterFunction.apply(aggregateAccessor.apply(aggregate));
    }

    public Number convert(S value)
    {
        return converterFunction.apply(value);
    }

    public int compareTo(T aggregate, S value)
    {
        var aggregateValue = new BigDecimal( converterFunction.apply(aggregateAccessor.apply(aggregate)).toString() );
        var givenValue = new BigDecimal( converterFunction.apply( value).toString());
        return aggregateValue.compareTo(givenValue);
    }

    public int compareTo2(T aggregate1, T aggregate2)
    {
        var aggregateValue1 = new BigDecimal( converterFunction.apply(aggregateAccessor.apply(aggregate1)).toString() );
        var aggregateValue2 = new BigDecimal( converterFunction.apply(aggregateAccessor.apply(aggregate2)).toString() );
        return aggregateValue1.compareTo(aggregateValue2);
    }


}

