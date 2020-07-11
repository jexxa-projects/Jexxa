package io.jexxa.tutorials.bookstore.domain.valueobject;

/**
 * Registrant element - this identifies the particular publisher or imprint. This may be up to 7 digits in length
 */
public class Registrant
{
    private final int value;

    public Registrant(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
