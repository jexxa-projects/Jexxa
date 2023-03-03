package io.jexxa.infrastructure.persistence.objectstore.metadata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;


@SuppressWarnings({"unused","java:S1452"})
public final class MetaTags
{
    /**
     * Factory method to create a MetaTag wich compares value of an aggregate using defined converter function
     *
     * @param accessor defines the method to get the value to be compared
     * @param converter defines the converter function converting the value into a number
     * @param <T> type of the aggregate
     * @param <V> type of the value
     * @return a MetaTag wich compares defined value of an aggregate
     */
    public static <T, V> NumericTag<T, V> numericTag(Function<T, V> accessor, Function<V, ? extends Number> converter)
    {
        return new NumericTag<>(accessor, converter);
    }


    /**
     * Factory method to create a MetaTag wich compares value of type {@link Number} of an aggregate
     *
     * @param accessorFunction returns the Instant
     * @param <T> type of the aggregate
     * @return a MetaTag wich compares an {@link Number} of an aggregate
     */
    public static <T, V extends Number> NumericTag<T, V> numericTag(Function<T,V> accessorFunction )
    {
        return new NumericTag<>(accessorFunction, element -> element);
    }

    /**
     * Factory method to create a MetaTag wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the Instant
     * @param <T> type of the aggregate
     * @return a MetaTag wich compares an {@link Instant} of an aggregate
     */
    @SuppressWarnings("java:S5411")
    public static <T> NumericTag<T, Boolean> booleanTag(Function<T, Boolean> accessor )
    {
        return new NumericTag<>(accessor, element -> element ? 1 : 0);
    }

    /**
     * Factory method to create a MetaTag wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the Instant
     * @param <T> type of the aggregate
     * @return a MetaTag wich compares an {@link Instant} of an aggregate
     */
    public static <T> InstantTag<T> instantTag(Function<T, Instant> accessor )
    {
        return new InstantTag<>(accessor);
    }

    /**
     * Factory method to create a MetaTag wich compares value of type {@link Instant} of an aggregate
     *
     * @param accessor returns the String
     * @param <T> type of the aggregate
     * @return a MetaTag wich compares an {@link String} of an aggregate
     */
    public static <T> StringTag<T, String> stringTag(Function<T, String> accessor )
    {
        return new StringTag<>(accessor, element -> element);
    }

    private static class InstantTag<T> extends NumericTag<T, Instant>
    {
        public static final int NANO = 1000000000;
        public InstantTag(Function<T, Instant> accessor)
        {
            super(accessor
                    , instant -> BigDecimal.valueOf(instant.getEpochSecond() * NANO).add( BigDecimal.valueOf(instant.getNano()))
                    );
        }

    }


    private MetaTags()
    {
        //private constructor
    }


}
