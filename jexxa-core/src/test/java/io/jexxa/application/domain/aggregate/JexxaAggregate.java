package io.jexxa.application.domain.aggregate;

import io.jexxa.application.annotation.Aggregate;
import io.jexxa.application.annotation.AggregateID;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

@Aggregate
public final class JexxaAggregate
{
    private final JexxaValueObject jexxaValueObject;

    private int internalValue;

    public static JexxaAggregate create(JexxaValueObject key)
    {
        return new JexxaAggregate(key);
    }

    public void setInternalValue(int value)
    {
        internalValue = value;
    }

    public int getInternalValue()
    {
        return internalValue;
    }


    @AggregateID
    public JexxaValueObject getKey()
    {
        return jexxaValueObject;
    }

    private JexxaAggregate(JexxaValueObject jexxaValueObject)
    {
        this.jexxaValueObject = jexxaValueObject;
    }
}
