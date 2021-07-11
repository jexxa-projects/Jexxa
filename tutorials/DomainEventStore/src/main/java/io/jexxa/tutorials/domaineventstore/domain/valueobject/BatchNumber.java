package io.jexxa.tutorials.domaineventstore.domain.valueobject;

import io.jexxa.addend.applicationcore.ValueObject;

@ValueObject
public class BatchNumber
{
    private final int value;
    public BatchNumber(int value)
    {
        this.value = value;
    }

    public double getValue()
    {
        return value;
    }
}
