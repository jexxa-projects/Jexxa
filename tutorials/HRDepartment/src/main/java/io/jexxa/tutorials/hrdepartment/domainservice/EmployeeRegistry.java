package io.jexxa.tutorials.hrdepartment.domainservice;

import java.util.List;

import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeNumber;

public interface EmployeeRegistry
{
    List<Employee> get();

    Employee get(EmployeeNumber employeeNumber);

    void add(Employee employee);

    void update(Employee employee);
}
