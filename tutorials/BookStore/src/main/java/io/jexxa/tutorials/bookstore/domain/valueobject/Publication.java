package io.jexxa.tutorials.bookstore.domain.valueobject;

/**
 * Publication element – this identifies the particular edition and format of a specific title. This may be up to 6 digits in length
 */
public class Publication
{
    private final int value;

    Publication(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
