package io.jexxa.tutorials.hrdepartment.infrastructure.drivenadapter.persistence;

import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.tutorials.hrdepartment.domain.aggregate.Employee;
import io.jexxa.tutorials.hrdepartment.domain.valueobject.EmployeeID;
import io.jexxa.tutorials.hrdepartment.domainservice.EmployeeRegistry;

public final class PersistendEmployeeResitry implements EmployeeRegistry
{
    private final IRepository<Employee, EmployeeID> irepository;

    private PersistendEmployeeResitry(IRepository<Employee, EmployeeID> irepository)
    {
        this.irepository = irepository;
    }

    @Override
    public List<Employee> get()
    {
        return irepository.get();
    }

    @Override
    public Employee get(EmployeeID employeeID)
    {
        return irepository.get(employeeID).orElseThrow(() -> new IllegalArgumentException("Unknown employee:  " + employeeID.getValue()));
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

    public static EmployeeRegistry createInstance(Properties properties)
    {
        return new PersistendEmployeeResitry(
                RepositoryManager.getInstance().getStrategy(Employee.class, Employee::getID, properties)
        );
    }
}
