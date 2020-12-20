package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Comparators
{
    public static class InstantComparator<T> extends Comparator<T, Instant>
    {
        public static final int NANO = 1000000000;
        public InstantComparator(Function<T, BigDecimal> converterFunctionT)
        {
            super(converterFunctionT
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }

    public static class NumberComparator<T, V extends Number> extends Comparator<T, V>
    {

        public NumberComparator(Function<T, Number> converterFunctionT)
        {
            super( aggregate -> new BigDecimal((converterFunctionT.apply(aggregate).toString()))
                    , element -> new BigDecimal( element.toString() ));
        }

    }

    private Comparators()
    {
        //private constrator
    }
}
