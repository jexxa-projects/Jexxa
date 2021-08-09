package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter;

import java.util.function.Function;

/**
 * A comparator provides a strategy to compare a specific element of an aggregate.
 *
 * Note: The specific value must be convertible to a number
 *
 * @param <T> Defines the type of the aggregate
 * @param <S> Defines the type of the value inside the aggregate
 */
public class NumericConverter<T, S>  extends Converter<T, S, Number>
{
    /**
     * Creates an Comparator object
     *
     * @param valueAccessor defines a function to access a specific value of the aggregate
     * @param valueIConverter defines a function that converts a searched value into a Number for comparison
     */
    public NumericConverter(Function<T, S> valueAccessor,
                            IConverter<S, ? extends Number> valueIConverter)
    {
        super(valueAccessor, valueIConverter);
    }

    @Override
    public Class<Number> getValueType()
    {
        return Number.class;
    }

}

