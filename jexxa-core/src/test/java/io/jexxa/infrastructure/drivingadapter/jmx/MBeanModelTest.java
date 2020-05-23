package io.jexxa.infrastructure.drivingadapter.jmx;

import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;

import com.google.gson.Gson;
import io.jexxa.TestTags;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestTags.UNIT_TEST)
class MBeanModelTest
{
    @SuppressWarnings({"SameParameterValue", "FieldCanBeLocal", "unused"})
    static class JexxaCompoundValueObject
    {
        static final JexxaCompoundValueObject DEFAULT_VALUE = new JexxaCompoundValueObject(42);

        private final JexxaValueObject firstValueObject;
        private final JexxaValueObject secondValueObject;

        JexxaCompoundValueObject(int value)
        {
            firstValueObject = new JexxaValueObject(value);
            secondValueObject = new JexxaValueObject(value);
        }
    }

    @Test
    void getDomainPath()
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
    void toJsonTemplatePrimitive()
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
    void stringToJsonTemplate()
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
    void toJsonTemplate()
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
    void toJsonTemplateComplexValue()
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
    void invokeSetSimpleValue()
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
    void invokeSetSimpleValueObject()
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
    void disabledMBeanModelMethods()
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
        var attribute = new Attribute("value", 42);
        Assertions.assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttribute(attribute));
        //Assert that we can not set any parameter
        var attributeList = new AttributeList();
        Assertions.assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttributes(attributeList));
    }

}
