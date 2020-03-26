package io.ddd.jexxa.core;


import java.util.Properties;

import io.ddd.jexxa.applicationcore.domainservice.IMulipleImplementationTest;
import io.ddd.jexxa.applicationcore.domainservice.ITestDomainService;
import org.junit.Assert;
import org.junit.Test;

/*
 * Rules for creating a driving adapter:
 * 1. Public Default constructor available
 * 2. Public constructor with Properties as attribute
 * 3. Public static method with return type if the requested interface
 * 4. Public static method with return type if the requested interface and Properties as argument  
 */
public class DrivenAdapterFactoryTest
{
    @Test
    public void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        var result = objectUnderTest.createDrivenAdapter(ITestDomainService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void multipleInterfaceImplementation() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        var result = objectUnderTest.createDrivenAdapter(IMulipleImplementationTest.class);

        //Assert
        Assert.assertNotNull(result);
    }

}
