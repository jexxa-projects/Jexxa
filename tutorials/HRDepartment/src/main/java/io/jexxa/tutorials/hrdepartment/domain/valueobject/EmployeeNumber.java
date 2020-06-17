package io.jexxa.tutorials.hrdepartment.domain.valueobject;

public class EmployeeNumber
{
    private final int value;

    public EmployeeNumber(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
