package io.jexxa.tutorials.hrdepartment.applicationservice;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;
import io.jexxa.tutorials.hrdepartment.domainservice.EmployeeRegistry;
import io.jexxa.tutorials.hrdepartment.domainservice.EmploymentService;

@SuppressWarnings("unused")
public class EmployeeService
{
    private final EmploymentService employmentService;
    private final EmployeeRegistry employeeRegistry;

    public EmployeeService(EmploymentService employmentService, EmployeeRegistry employeeRegistry)
    {
        this.employmentService = employmentService;
        this.employeeRegistry = employeeRegistry;
    }

    public EmployeeID createEmployee()
    {
        var newEmployee = Employee.create( getNextEmployeeID() );
        
        employeeRegistry.add(newEmployee);

        return newEmployee.getID();
    }


    public void stopEmployment(EmployeeID employeeID, Date date)
    {
        var employee = employeeRegistry.get(employeeID);
        var updatedStatus = employee.stopEmployment(date);
        employeeRegistry.update(employee);
        employmentService.announceStoppedEmployment(updatedStatus);
    }

    public void startEmployment(EmployeeID employeeID, Date date)
    {
        var employee = employeeRegistry.get(employeeID);
        var updatedStatus = employee.startEmployment(date);
        employeeRegistry.update(employee);
        employmentService.announceStartedEmployment(updatedStatus);
    }

    public List<EmployeeID> getAllEmployees()
    {
        return employeeRegistry
                .get()
                .stream()
                .map(Employee::getID)
                .collect(Collectors.toList());
    }

    private EmployeeID getNextEmployeeID()
    {
        return getAllEmployees()
                .stream()
                .max(Comparator.comparing(EmployeeID::getValue))
                .orElse(new EmployeeID(1));
    }

}
