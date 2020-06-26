package io.jexxa.tutorials.hrdepartment.domain.domainevent;

import java.util.Date;

import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;

public class EmploymentStarted
{
    private final EmployeeID employeeID;
    private final Date startedDate;

    public EmploymentStarted(EmployeeID employeeID, Date startedDate)
    {
        this.employeeID = employeeID;
        this.startedDate = startedDate;
    }

    public Date getStartedDate()
    {
        return startedDate;
    }

    public EmployeeID getEmployeeID()
    {
        return employeeID;
    }
}
