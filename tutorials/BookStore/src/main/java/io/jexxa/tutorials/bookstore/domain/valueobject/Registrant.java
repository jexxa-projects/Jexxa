package io.jexxa.tutorials.bookstore.domain.valueobject;


import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * Registrant element - this identifies the particular publisher or imprint. This may be up to 7 digits in length
 */
public class Registrant
{
    private final int value;

    public Registrant(int value)
    {
        Validate.inclusiveBetween(0, 9999999, value, "Invalid Registrant not within valid range.");
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
        Registrant that = (Registrant) o;
        return value == that.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }
}
