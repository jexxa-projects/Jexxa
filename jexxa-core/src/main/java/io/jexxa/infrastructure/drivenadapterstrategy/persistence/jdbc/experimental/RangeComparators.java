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
        public InstantRangeComparator(Function<T, BigDecimal> converterFunctionT)
        {
            super(converterFunctionT
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }

    public static class NumberRangeComparator<T, V extends Number> extends RangeComparator<T, V>
    {

        public NumberRangeComparator(Function<T, Number> converterFunctionT)
        {
            super( aggregate -> new BigDecimal((converterFunctionT.apply(aggregate).toString()))
                    , element -> new BigDecimal( element.toString() ));
        }

    }

    public static <U, V extends Number> RangeComparator<U,V>
    createNumberComparator ( Function<U, Number> converterFunctionT )
    {
       return new NumberRangeComparator<>(converterFunctionT);
    }

    public static <U, V>  RangeComparator<U,V> create
            (
                    Function<U, ? extends Number> converterFunctionT,
                    Function<V, ? extends Number> converterFunctionS
            )
    {
        return new RangeComparator<>(converterFunctionT, converterFunctionS);
    }


    private RangeComparators()
    {
        //private constructor
    }


}
