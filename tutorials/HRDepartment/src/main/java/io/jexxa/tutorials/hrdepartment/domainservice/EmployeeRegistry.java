package io.jexxa.tutorials.hrdepartment.domainservice;

import java.util.List;

import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;

public interface EmployeeRegistry
{
    List<Employee> get();

    Employee get(EmployeeID employeeID);

    void add(Employee employee);

    void update(Employee employee);
}
