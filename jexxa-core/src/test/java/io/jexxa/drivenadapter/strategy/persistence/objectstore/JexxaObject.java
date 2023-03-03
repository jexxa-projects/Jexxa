package io.jexxa.drivenadapter.strategy.persistence.objectstore;

import io.jexxa.application.domain.model.JexxaEntity;
import io.jexxa.application.domain.model.JexxaValueObject;

import java.util.Objects;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public final class JexxaObject
{
    private final JexxaEntity jexxaEntity;
    private final JexxaValueObject jexxaValueObject;
    private JexxaValueObject optionalJexxaValue;
    private String optionalString;
    private final String internalString;

    public void setOptionalValue(JexxaValueObject optionalJexxaValue)
    {
        this.optionalJexxaValue = optionalJexxaValue;
    }

    public void setOptionalString(String optionalString)
    {
        this.optionalString = optionalString;
    }

    public String getOptionalString()
    {
        return optionalString;
    }

    public String getString()
    {
        return internalString;
    }

    public JexxaValueObject getOptionalValue()
    {
        return optionalJexxaValue;
    }

    private JexxaObject(JexxaValueObject jexxaValueObject, String internalString)
    {
        this.jexxaEntity = JexxaEntity.create(jexxaValueObject);
        this.jexxaValueObject = jexxaValueObject;
        this.internalString = internalString;
        this.optionalString = null;
        this.optionalJexxaValue = null;
    }

    // Create a sequence of chars of alphabet 'A' .. 'Z', 'AA', ...
    public static String createCharSequence(int n) {
        var counter = n;
        char[] buf = new char[(int) floor(log(25 * (counter + 1)) / log(26))];
        for (int i = buf.length - 1; i >= 0; i--)
        {
            counter--;
            buf[i] = (char) ('A' + counter % 26);
            counter /= 26;
        }
        return new String(buf);
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
        return new JexxaObject(key, createCharSequence(key.getValue()));
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
