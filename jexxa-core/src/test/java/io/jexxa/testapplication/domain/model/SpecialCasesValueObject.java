package io.jexxa.testapplication.domain.model;

import java.util.Objects;

/**
 * This ValueObject include following special cases:
 * <ul>
 *     <li> private field without public getter</li>
 *     <li> private field with null</li>
 * </ul>
 */
@SuppressWarnings("SameParameterValue")
public final class SpecialCasesValueObject
{

    public static final SpecialCasesValueObject SPECIAL_CASES_VALUE_OBJECT = new SpecialCasesValueObject(1);

    private final int valueWithoutGetter;
    private final String nullValue = null;

    private SpecialCasesValueObject(int value)
    {
        this.valueWithoutGetter = value;
    }

    @SuppressWarnings({"ConstantConditions", "unused"})
    public String getNullValue()
    {
        return nullValue;
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
        SpecialCasesValueObject that = (SpecialCasesValueObject) o;
        return valueWithoutGetter == that.valueWithoutGetter;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(valueWithoutGetter);
    }
}
