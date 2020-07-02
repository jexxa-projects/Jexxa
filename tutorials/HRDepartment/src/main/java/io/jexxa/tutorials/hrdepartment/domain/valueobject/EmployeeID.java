package io.jexxa.tutorials.hrdepartment.domain.valueobject;

public class EmployeeID
{
    private final int value;

    public EmployeeID(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
