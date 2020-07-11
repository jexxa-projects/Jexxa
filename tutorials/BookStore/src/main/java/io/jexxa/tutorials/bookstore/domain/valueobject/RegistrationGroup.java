package io.jexxa.tutorials.bookstore.domain.valueobject;

/**  Registration group element – this identifies the particular country, geographical region, or language area participating in the ISBN system. This element may be between 1 and 5 digits in length
 */

public class RegistrationGroup
{
    private final int value;

    public RegistrationGroup(int value)
    {
     this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
