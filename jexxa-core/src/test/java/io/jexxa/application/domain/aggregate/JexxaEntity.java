package io.jexxa.application.domain.aggregate;

import io.jexxa.application.annotation.Aggregate;
import io.jexxa.application.annotation.AggregateID;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

@Aggregate
public final class JexxaEntity
{
    private final JexxaValueObject jexxaValueObject;

    private int internalValue;

    public static JexxaEntity create(JexxaValueObject key)
    {
        return new JexxaEntity(key);
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

    private JexxaEntity(JexxaValueObject jexxaValueObject)
    {
        this.jexxaValueObject = jexxaValueObject;
    }
}
