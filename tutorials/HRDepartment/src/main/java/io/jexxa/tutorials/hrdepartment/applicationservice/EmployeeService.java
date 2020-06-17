package io.jexxa.tutorials.hrdepartment.applicationservice;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeNumber;
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

    public EmployeeNumber createEmployee()
    {
        var highestEmployeeNumber = getAllEmployees()
                .stream()
                .sorted(Comparator.comparing(EmployeeNumber::getValue))
                .reduce((first, second) -> second);
        
        var newEmployee = highestEmployeeNumber
                .map(employeeNumber -> Employee.create(new EmployeeNumber(employeeNumber.getValue() + 1)))
                .orElseGet(() -> Employee.create(new EmployeeNumber(1)));

        employeeRegistry.add(newEmployee);

        return newEmployee.getID();
    }


    public void stopEmployment(EmployeeNumber employeeNumber, Date date)
    {
        var employee = employeeRegistry.get(employeeNumber);
        var updatedStatus = employee.stopEmployment(date);
        employeeRegistry.update(employee);
        employmentService.announceStoppedEmployment(updatedStatus);
    }

    public void startEmployment(EmployeeNumber employeeNumber, Date date)
    {
        var employee = employeeRegistry.get(employeeNumber);
        var updatedStatus = employee.startEmployment(date);
        employeeRegistry.update(employee);
        employmentService.announceStartedEmployment(updatedStatus);

    }

    public List<EmployeeNumber> getAllEmployees()
    {
        return employeeRegistry
                .get()
                .stream()
                .map(Employee::getID)
                .collect(Collectors.toList());
    }

}
