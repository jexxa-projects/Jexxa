package io.jexxa.tutorials.bookstore.domain.valueobject;

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


    public static final Prefix PREFIX_978 = new Prefix(978);
    public static final Prefix PREFIX_979 = new Prefix(979);

}
