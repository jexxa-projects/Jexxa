package io.jexxa.tutorials.hrdepartment.applicationservice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeServiceTest
{
    JexxaMain jexxaMain;

    EmployeeService objectUnderTest;

    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(getClass().getSimpleName());

        jexxaMain.addToApplicationCore("io.jexxa.tutorials.hrdepartment.domainservice")
                .addToInfrastructure("io.jexxa.tutorials.hrdepartment.infrastructure");

        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = jexxaMain.getInstanceOfPort(EmployeeService.class);
    }

    @Test
    void addEmployee()
    {
        //Arrange - nothing

        //Act
        var result = objectUnderTest.createEmployee();

        //Assert
        assertNotNull(result);
        assertEquals(1, objectUnderTest.getAllEmployees().size());
    }
}