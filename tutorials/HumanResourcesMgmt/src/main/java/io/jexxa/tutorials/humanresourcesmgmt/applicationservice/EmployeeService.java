package io.jexxa.tutorials.humanresourcesmgmt.applicationservice;

import java.util.ArrayList;
import java.util.List;

import io.jexxa.tutorials.humanresourcesmgmt.domain.valueobject.EmployeeNumber;

public class EmployeeService
{
    public EmployeeNumber createEmployee()
    {
        return new EmployeeNumber();
    }

    public void retireEmployee(EmployeeNumber employeeNumber)
    {

    }

    public void leaveEmployee(EmployeeNumber employeeNumber)
    {

    }

    public List<EmployeeNumber> getAllEmployees()
    {
        return new ArrayList<>();
    }

}
