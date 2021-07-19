package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import java.util.Objects;
import java.util.Optional;

import io.jexxa.application.annotation.Aggregate;
import io.jexxa.application.domain.aggregate.JexxaEntity;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

@Aggregate
public class JexxaObject
{
    private final JexxaEntity jexxaEntity;
    private final JexxaValueObject jexxaValueObject;
    private JexxaValueObject optionalJexxaValue;

    public JexxaObject setoptionalJexxaValue(JexxaValueObject optionalJexxaValue)
    {
        this.optionalJexxaValue = optionalJexxaValue;
        return this;
    }

    public JexxaObject setOptionalString(String optionalString)
    {
        this.optionalString = optionalString;
        return this;
    }

    private String optionalString;

    public Optional<String> getOptionalString()
    {
        return Optional.ofNullable(optionalString);
    }

    public Optional<JexxaValueObject> getOptionalJexxaValue()
    {
        return Optional.ofNullable(optionalJexxaValue);
    }

    private JexxaObject(JexxaValueObject jexxaValueObject)
    {
        this.jexxaEntity = JexxaEntity.create(jexxaValueObject);
        this.jexxaValueObject = jexxaValueObject;
        this.optionalString = null;
        this.optionalJexxaValue = null;
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

    public static JexxaObject create(JexxaValueObject key)
    {
        return new JexxaObject(key);
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
        JexxaObject that = (JexxaObject) o;
        return Objects.equals(getKey(), that.getKey());     // Only compare keys
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jexxaValueObject);
    }
}
