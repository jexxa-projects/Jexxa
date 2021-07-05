package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;


@SuppressWarnings({"unused","java:S1452"})
public class RangeComparators
{
    public static class InstantRangeComparator<T> extends RangeComparator<T, Instant>
    {
        public static final int NANO = 1000000000;
        public InstantRangeComparator(Function<T, Instant> accessorFunction)
        {
            super(accessorFunction
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }

    public static class NumberRangeComparator<T, V extends Number> extends RangeComparator<T, V>
    {
        public NumberRangeComparator(Function<T, V> accessorFunction)
        {
            super( accessorFunction, element -> element);
        }
    }



    private RangeComparators()
    {
        //private constructor
    }


}
