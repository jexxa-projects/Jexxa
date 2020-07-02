package io.jexxa.tutorials.hrdepartment.domain.domainevent;

import java.time.LocalDate;

import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;

public class EmploymentStarted
{
    private final EmployeeID employeeID;
    private final LocalDate startedDate;

    public EmploymentStarted(EmployeeID employeeID, LocalDate startedDate)
    {
        this.employeeID = employeeID;
        this.startedDate = startedDate;
    }

    public LocalDate getStartedDate()
    {
        return startedDate;
    }

    public EmployeeID getEmployeeID()
    {
        return employeeID;
    }
}
