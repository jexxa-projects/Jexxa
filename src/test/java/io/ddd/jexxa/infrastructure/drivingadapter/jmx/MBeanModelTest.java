package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;

import com.google.gson.Gson;
import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag("unit-test")
public class MBeanModelTest
{
    @SuppressWarnings({"SameParameterValue", "FieldCanBeLocal", "unused"})
    static class JexxaCompoundValueObject
    {
        public static final JexxaCompoundValueObject DEFAULT_VALUE = new JexxaCompoundValueObject(42);

        private final JexxaValueObject firstValueObject;
        private final JexxaValueObject secondValueObject;

        JexxaCompoundValueObject(int value)
        {
            firstValueObject = new JexxaValueObject(value);
            secondValueObject = new JexxaValueObject(value);
        }
    }

    @Test
    public void getDomainPath()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        Assertions.assertEquals("MBeanModelTest:type=ApplicationService,name=SimpleApplicationService", objectUnderTest.getDomainPath());
    }

    @Test
    public void toJsonTemplatePrimitive()
    {
        //Arrange
        String integerTemplate = "{\"int\":\"<int>\"}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(int.class);

        //Assert
        Assertions.assertEquals(integerTemplate, result);
    }

    @Test
    public void stringToJsonTemplate()
    {
        //Arrange
        String stringTemplate = "{\"String\":\"<String>\"}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(String.class);

        //Assert
        Assertions.assertEquals(stringTemplate, result);
    }

    @Test
    public void toJsonTemplate()
    {
        //Arrange
        String jexxaValueObjectTemplate = "{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(JexxaValueObject.class);

        //Assert
        Assertions.assertEquals(jexxaValueObjectTemplate, result);
    }

    @Test
    public void toJsonTemplateComplexValue()
    {
        //Arrange
        String jexxaValueObjectTemplate = "{\"firstValueObject\":{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"},\"secondValueObject\":{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"}}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(JexxaCompoundValueObject.class);

        //Assert
        Assertions.assertEquals(jexxaValueObjectTemplate, result);
    }

    @Test
    public void invokeSetSimpleValue()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValue";
        var newValue = 5;
        var gson = new Gson();


        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{gson.toJson(newValue)}, new String[0]);

        //Assert
        Assertions.assertEquals(newValue, applicationService.getSimpleValue());
    }


    @Test
    public void invokeSetSimpleValueObject()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValueObject";
        var newValue = new JexxaValueObject(5);
        var gson = new Gson();

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{gson.toJson(newValue)}, new String[0]);

        //Assert
        Assertions.assertEquals(newValue.getValue(), applicationService.getSimpleValueObject().getValue());
    }


    @Test
    public void disabledMBeanModelMethods()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanModel.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanModel(applicationService, properties);

        //Assert that we get no Attributes because we only provide access to public methods
        Assertions.assertNull(objectUnderTest.getAttribute(""));
        //Assert that we get no Attributes because we only provide access to public methods
        Assertions.assertTrue(objectUnderTest.getAttributes(new String[0]).isEmpty());

        //Assert that we can not set any parameter  
        Assertions.assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttribute(new Attribute("value", 42)));
        //Assert that we can not set any parameter
        Assertions.assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttributes(new AttributeList()));
    }

}
