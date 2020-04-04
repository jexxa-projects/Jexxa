package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import java.util.Properties;

import io.ddd.jexxa.dummyapplication.annotation.ApplicationService;
import io.ddd.jexxa.dummyapplication.applicationservice.SimpleApplicationService;
import org.junit.Assert;
import org.junit.Test;

public class MBeanModelTest
{
    @Test
    public void getSubType()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        Assert.assertEquals("MBeanModelTest:type=ApplicationService,name=SimpleApplicationService", objectUnderTest.getDomainPath());
    }
}
