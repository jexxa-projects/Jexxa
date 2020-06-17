package io.jexxa.tutorials.hrdepartment.domain.domainevent;

import java.util.Date;

import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeNumber;

public class EmploymentStopped
{
    private final EmployeeNumber employeeNumber;
    private final Date startedDate;

    public EmploymentStopped(EmployeeNumber employeeNumber, Date startedDate)
    {
        this.employeeNumber = employeeNumber;
        this.startedDate = startedDate;
    }

    public Date getStartedDate()
    {
        return startedDate;
    }

    public EmployeeNumber getEmployeeNumber()
    {
        return employeeNumber;
    }
}
