package io.jexxa.tutorials.hrdepartment.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeNumber;
import io.jexxa.tutorials.hrdepartment.domainservice.EmployeeRegistry;

public final class PersistendEmployeeResitry implements EmployeeRegistry
{
    private final IRepository<Employee, EmployeeNumber> irepository;

    private PersistendEmployeeResitry(IRepository<Employee, EmployeeNumber> irepository)
    {
        this.irepository = irepository;
    }

    @Override
    public List<Employee> get()
    {
        return irepository.get();
    }

    @Override
    public Employee get(EmployeeNumber employeeNumber)
    {
        return irepository.get(employeeNumber).orElseThrow(() -> new IllegalArgumentException("Unknown employee:  " + employeeNumber.getValue()));
    }

    @Override
    public void add(Employee employee)
    {
        irepository.add(employee);
    }

    @Override
    public void update(Employee employee)
    {
        irepository.update(employee);
    }

    public static EmployeeRegistry getInstance(Properties properties)
    {
        return new PersistendEmployeeResitry(
                RepositoryManager.getInstance().getStrategy(Employee.class, Employee::getID, properties)
        );
    }
}
