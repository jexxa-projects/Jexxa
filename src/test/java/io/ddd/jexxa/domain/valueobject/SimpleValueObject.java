package io.ddd.jexxa.domain.valueobject;

import io.ddd.stereotype.applicationcore.ValueObject;

@ValueObject
public class SimpleValueObject
{
    private int value;
    double valueInPercent;

    public SimpleValueObject(int value) {
        this.value = value;
        this.valueInPercent = value / 100.0;
    }

    public int getValue()
    {
        return value;
    }

    public double getValueInPercent()
    {
        return valueInPercent;
    }
}
