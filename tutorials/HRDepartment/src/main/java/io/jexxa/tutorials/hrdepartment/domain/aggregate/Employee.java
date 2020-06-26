package io.jexxa.tutorials.hrdepartment.domain.aggregate;

import java.util.Date;

import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStarted;
import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStopped;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;

public final class Employee
{
    private final EmployeeID employeeID;

    private Date stopEmployment = null;
    private Date startEmployment = null;

    public EmployeeID getID()
    {
        return employeeID;
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

    private Employee(EmployeeID employeeID)
    {
        this.employeeID = employeeID;
    }


    public static Employee create(EmployeeID employeeID)
    {
        return new Employee(employeeID);
    }

}
