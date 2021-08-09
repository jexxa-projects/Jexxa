package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;


@SuppressWarnings({"unused","java:S1452"})
public class Converters
{
    /**
     * Factory method to create comparator wich compares value of an aggregate using defined converter function
     *
     * @param accessor defines the method to get the value to be compared
     * @param converter defines the converter function converting the value into a number
     * @param <T> type of the aggregate
     * @param <V> type of the value
     * @return Comparator wich compares defined value of an aggregate
     */
    public static <T, V> NumericConverter<T, V> numberConverter(Function<T, V> accessor, IConverter<V, ? extends Number> converter)
    {
        return new NumericConverter<>(accessor, converter);
    }

    /**
     * Factory method to create comparator wich compares value of type {@link Number} of an aggregate
     *
     * @param accessorfunction returns the Instant
     * @param <T> type of the aggregate
     * @return Comparator wich compares an {@link Number} of an aggregate
     */
    public static <T, V extends Number> NumericConverter<T, V> numberConverter(Function<T,V> accessorfunction )
    {
        return new NumericConverter<>(accessorfunction, element -> element);
    }

    /**
     * Factory method to create comparator wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the Instant
     * @param <T> type of the aggregate
     * @return Comparator wich compares an {@link Instant} of an aggregate
     */
    @SuppressWarnings("java:S5411")
    public static <T> NumericConverter<T, Boolean> booleanConverter(Function<T, Boolean> accessor )
    {
        return new NumericConverter<>(accessor, element -> element ? 1 : 0);
    }

    /**
     * Factory method to create comparator wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the Instant
     * @param <T> type of the aggregate
     * @return Comparator wich compares an {@link Instant} of an aggregate
     */
    public static <T> InstantConverter<T> instantConverter(Function<T, Instant> accessor )
    {
        return new InstantConverter<>(accessor);
    }

    /**
     * Factory method to create comparator wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the String
     * @param <T> type of the aggregate
     * @return Comparator wich compares an {@link String} of an aggregate
     */
    public static <T> StringConverter<T, String> stringConverter(Function<T, String> accessor )
    {
        return new StringConverter<>(accessor, element -> element);
    }

    private static class InstantConverter<T> extends NumericConverter<T, Instant>
    {
        public static final int NANO = 1000000000;
        public InstantConverter(Function<T, Instant> accessor)
        {
            super(accessor
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }


    private Converters()
    {
        //private constructor
    }


}
