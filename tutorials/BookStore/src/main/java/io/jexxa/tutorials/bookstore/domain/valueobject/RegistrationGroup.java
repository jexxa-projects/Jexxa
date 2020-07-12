package io.jexxa.tutorials.bookstore.domain.valueobject;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**  Registration group element – this identifies the particular country, geographical region, or language area participating in the ISBN system. This element may be between 1 and 5 digits in length
 */

public class RegistrationGroup
{
    private final int value;

    public RegistrationGroup(int value)
    {
        Validate.inclusiveBetween(0, 99999, value, "Registrant Group element not within valid range");
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
        RegistrationGroup that = (RegistrationGroup) o;
        return value == that.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }
}
