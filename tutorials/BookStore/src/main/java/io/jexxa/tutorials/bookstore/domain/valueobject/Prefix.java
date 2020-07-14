package io.jexxa.tutorials.bookstore.domain.valueobject;


import java.util.Objects;

import org.apache.commons.lang.Validate;

/**
 * Prefix element – currently this can only be either 978 or 979. It is always 3 digits in length
 */
public class Prefix
{
    public static final Prefix PREFIX_978 = new Prefix(978);
    public static final Prefix PREFIX_979 = new Prefix(979);

    private final int value;

    public Prefix(int value)
    {
        Validate.isTrue( value == 978 || value == 979);
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Prefix prefix = (Prefix) o;
        return value == prefix.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

}
