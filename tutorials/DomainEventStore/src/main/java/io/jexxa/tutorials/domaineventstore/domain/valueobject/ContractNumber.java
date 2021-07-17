package io.jexxa.tutorials.domaineventstore.domain.valueobject;

import io.jexxa.addend.applicationcore.ValueObject;

@ValueObject
public class ContractNumber
{
    private final int value;
    public ContractNumber(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
