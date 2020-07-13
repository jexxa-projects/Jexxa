package io.jexxa.tutorials.bookstore.domain.valueobject;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class CheckDigit
{
    final int value;

    public CheckDigit(int value)
    {
        Validate.inclusiveBetween(0, 9, value, "ISBN checksum not in boundaries");
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
        CheckDigit that = (CheckDigit) o;
        return value == that.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }
}
