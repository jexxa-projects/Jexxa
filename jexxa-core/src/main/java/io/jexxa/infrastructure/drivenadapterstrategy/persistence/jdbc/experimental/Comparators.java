package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;


@SuppressWarnings({"unused","java:S1452"})
public class Comparators
{
    public static  <T, V> Comparator<T, V> keyComparator()
    {
        return null;
    }

    public static  <T, V> Comparator<T, V> valueComparator()
    {
        return null;
    }

    public static <T, V> Comparator<T, V> aggregateComparator(Function<T, V> accessorFunction, Function<V, ? extends Number> converterFunction)
    {
        return new Comparator<>(accessorFunction, converterFunction);
    }

    public static <T> InstantComparator<T> instantComparator(Function<T, Instant> accessorFunction )
    {
        return new InstantComparator<>(accessorFunction);
    }

    public static <T, V extends Number> NumberComparator<T, V> numberComparator(Function<T,V> accessorFunction )
    {
        return new NumberComparator<>(accessorFunction);
    }

    public static class InstantComparator<T> extends Comparator<T, Instant>
    {
        public static final int NANO = 1000000000;
        public InstantComparator(Function<T, Instant> accessorFunction)
        {
            super(accessorFunction
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }

    public static class NumberComparator<T, V extends Number> extends Comparator<T, V>
    {
        public NumberComparator(Function<T, V> accessorFunction)
        {
            super( accessorFunction, element -> element);
        }
    }



    private Comparators()
    {
        //private constructor
    }


}
