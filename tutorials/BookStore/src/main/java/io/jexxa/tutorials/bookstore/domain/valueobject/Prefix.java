package io.jexxa.tutorials.bookstore.domain.valueobject;


import java.util.Objects;

/**
 * Prefix element – currently this can only be either 978 or 979. It is always 3 digits in length
 */
public class Prefix
{
    private final int value;

    private Prefix(int value)
    {
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

    public static final Prefix PREFIX_978 = new Prefix(978);
    public static final Prefix PREFIX_979 = new Prefix(979);

}
