package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

public class Comparator<T, S>
{
    private final Function<T, S> valueAccessor;
    private final Function<S, ? extends Number> valueConverter;

    public Comparator(Function<T, S> valueAccessor,
                      Function<S, ? extends Number> valueConverter)
    {
        this.valueAccessor = Objects.requireNonNull( valueAccessor );
        this.valueConverter = Objects.requireNonNull( valueConverter );
    }

    public S getValue(T aggregate)
    {
        Objects.requireNonNull(aggregate);
        return valueAccessor.apply(aggregate);
    }

    public Number convertFrom(T aggregate)
    {
        Objects.requireNonNull(aggregate);
        return valueConverter.apply(valueAccessor.apply(aggregate));
    }

    public Number convert(S value)
    {
        Objects.requireNonNull(value);
        return valueConverter.apply(value);
    }

    public int compareTo(T aggregate, S value)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(value);

        var aggregateValue = new BigDecimal( valueConverter.apply(valueAccessor.apply(aggregate)).toString() );
        var givenValue = new BigDecimal( valueConverter.apply( value).toString());
        return aggregateValue.compareTo(givenValue);
    }

    public int compareTo2(T aggregate1, T aggregate2)
    {
        Objects.requireNonNull(aggregate1);
        Objects.requireNonNull(aggregate2);

        var aggregateValue1 = new BigDecimal( valueConverter.apply(valueAccessor.apply(aggregate1)).toString() );
        var aggregateValue2 = new BigDecimal( valueConverter.apply(valueAccessor.apply(aggregate2)).toString() );
        return aggregateValue1.compareTo(aggregateValue2);
    }


}

