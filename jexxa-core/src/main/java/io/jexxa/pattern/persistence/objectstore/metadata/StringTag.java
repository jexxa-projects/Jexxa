package io.jexxa.pattern.persistence.objectstore.metadata;

import java.util.function.Function;

/**
 * This class represents and uses a string representation to perform all query operations.
 *
 * @param <T> Defines the type of the aggregate
 * @param <S> Defines the type of the value inside the aggregate
 */
public class StringTag<T, S>  extends MetaTag<T, S, String>
{
    /**
     * Creates a StringTag
     *
     * @param valueAccessor defines a function to access a specific value of the aggregate
     * @param valueConverter defines a function that converts the data into a string
     */
    StringTag(Function<T, S> valueAccessor,
              Function<S, String> valueConverter)
    {
        super(valueAccessor, valueConverter);
    }

    @Override
    public Class<String> getTagType()
    {
        return String.class;
    }

}

