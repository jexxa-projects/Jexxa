package io.jexxa.testapplication.domain.model;

import java.util.Objects;

public final class JexxaAggregate
{
    private final JexxaEntity jexxaEntity;
    private final JexxaValueObject jexxaValueObject;

    private JexxaAggregate(JexxaValueObject jexxaValueObject)
    {
        this.jexxaEntity = JexxaEntity.create(jexxaValueObject);
        this.jexxaValueObject = jexxaValueObject;
    }

    public void setInternalValue(int value)
    {
        jexxaEntity.setInternalValue(value);
    }

    public int getInternalValue()
    {
        return jexxaEntity.getInternalValue();
    }

    public JexxaValueObject getKey()
    {
        return jexxaValueObject;
    }

    public static JexxaAggregate create(JexxaValueObject key)
    {
        return new JexxaAggregate(key);
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
        JexxaAggregate that = (JexxaAggregate) o;
        return Objects.equals(getKey(), that.getKey());     // Only compare keys
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jexxaValueObject);
    }
}
