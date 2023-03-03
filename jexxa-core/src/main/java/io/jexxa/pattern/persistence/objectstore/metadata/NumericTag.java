package io.jexxa.pattern.persistence.objectstore.metadata;

import java.util.function.Function;

/**
 * This class provides a strategy to a specific element of an aggregate as a numeric MetaTag.
 * <p>
 * Note: The specific value must be convertible to a number
 *
 * @param <T> Defines the type of the aggregate
 * @param <S> Defines the type of the value inside the aggregate
 */
public class NumericTag<T, S>  extends MetaTag<T, S, Number>
{
    /**
     * Creates a NumericTag
     *
     * @param valueAccessor defines a function to access a specific value of the aggregate
     * @param valueConverter defines a function that converts a searched value into a Number for comparison
     */
    public NumericTag(Function<T, S> valueAccessor,
                      Function<S, ? extends Number> valueConverter)
    {
        super(valueAccessor, valueConverter);
    }

    @Override
    public Class<Number> getTagType()
    {
        return Number.class;
    }

}

