package io.jexxa.tutorials.bookstore.domain.valueobject;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * Publication element – this identifies the particular edition and format of a specific title. This may be up to 6 digits in length
 */
public class Publication
{
    private final int value;

    Publication(int value)
    {
        Validate.inclusiveBetween(0, 999999, value, "Publication element not within valid range!");
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
        Publication that = (Publication) o;
        return value == that.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }
}
