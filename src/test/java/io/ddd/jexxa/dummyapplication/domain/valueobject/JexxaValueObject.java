package io.ddd.jexxa.dummyapplication.domain.valueobject;

import io.ddd.jexxa.dummyapplication.annotation.*;

@ValueObject
public class JexxaValueObject
{
    private final int value;
    final double valueInPercent;

    public JexxaValueObject(int value) {
        this.value = value;
        this.valueInPercent = value / 100.0;
    }

    public int getValue()
    {
        return value;
    }

    @SuppressWarnings("unused")
    public double getValueInPercent()
    {
        return valueInPercent;
    }
}
