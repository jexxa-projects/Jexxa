package io.jexxa.application.domain.aggregate;

import io.jexxa.application.domain.valueobject.JexxaValueObject;

import java.util.Objects;

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


    public JexxaValueObject getKey()
    {
        return jexxaValueObject;
    }

    private JexxaEntity(JexxaValueObject jexxaValueObject)
    {
        this.jexxaValueObject = jexxaValueObject;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        JexxaEntity that = (JexxaEntity) o;
        return Objects.equals(getKey(), that.getKey());     // Only compare keys
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jexxaValueObject);
    }
}
