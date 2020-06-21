package io.jexxa.tutorials.hrdepartment.domain.aggregate;

import java.util.Date;

import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStarted;
import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStopped;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeNumber;

public final class Employee
{
    private final EmployeeNumber employeeNumber;

    private Date stopEmployment = null;
    private Date startEmployment = null;

    public EmployeeNumber getID()
    {
        return employeeNumber;
    }


    public EmploymentStopped stopEmployment(Date date)
    {
        stopEmployment = date;
        return new EmploymentStopped(getID(), date);
    }

    public EmploymentStarted startEmployment(Date date)
    {
        startEmployment = date;
        return new EmploymentStarted(getID(), date);
    }

    public Date getStartEmployment()
    {
        return startEmployment;
    }


    public Date getStopEmployment()
    {
        return stopEmployment;
    }

    private Employee(EmployeeNumber employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }


    public static Employee create(EmployeeNumber employeeNumber)
    {
        return new Employee(employeeNumber);
    }

}
