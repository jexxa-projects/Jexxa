package io.jexxa.tutorials.bookstore.domain.valueobject;

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
}
