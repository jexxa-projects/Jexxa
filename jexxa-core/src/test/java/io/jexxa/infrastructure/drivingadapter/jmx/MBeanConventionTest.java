package io.jexxa.infrastructure.drivingadapter.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;

import com.google.gson.Gson;
import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class MBeanConventionTest
{
    @SuppressWarnings({"SameParameterValue", "FieldCanBeLocal", "unused"})
    static class JexxaCompoundValueObject
    {
        private static final JexxaCompoundValueObject DEFAULT_VALUE = new JexxaCompoundValueObject(42);

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
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        assertEquals(properties.get(MBeanConvention.JEXXA_CONTEXT_NAME) + ":type=ApplicationService,name=SimpleApplicationService",
                objectUnderTest.getDomainPath());
    }

    @Test
    void toJsonTemplatePrimitive()
    {
        //Arrange
        String integerTemplate = "<int>";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(int.class);

        //Assert
        assertEquals(integerTemplate, result);
    }

    @Test
    void stringToJsonTemplate()
    {
        //Arrange
        String stringTemplate = "<String>";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(String.class);

        //Assert
        assertEquals(stringTemplate, result);
    }

    @Test
    void toJsonTemplate()
    {
        //Arrange
        String jexxaValueObjectTemplate = "{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(JexxaValueObject.class);

        //Assert
        assertEquals(jexxaValueObjectTemplate, result);
    }

    @Test
    void toJsonTemplateComplexValue()
    {
        //Arrange
        String jexxaValueObjectTemplate = "{\"firstValueObject\":{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"},\"secondValueObject\":{\"value\":\"<int>\",\"valueInPercent\":\"<double>\"}}";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        var result = objectUnderTest.toJsonTemplate(JexxaCompoundValueObject.class);

        //Assert
        assertEquals(jexxaValueObjectTemplate, result);
    }

    @Test
    void invokeSetSimpleValue()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValue";
        var newValue = 5;
        var gson = new Gson();


        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{gson.toJson(newValue)}, new String[0]);

        //Assert
        assertEquals(newValue, applicationService.getSimpleValue());
    }


    @Test
    void invokeSetSimpleValueObject()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValueObject";
        var newValue = new JexxaValueObject(5);
        var gson = new Gson();

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{gson.toJson(newValue)}, new String[0]);

        //Assert
        assertEquals(newValue.getValue(), applicationService.getSimpleValueObject().getValue());
    }


    @Test
    void disabledMBeanModelMethods()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(MBeanConvention.JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Assert that we get no Attributes because we only provide access to public methods
        assertNull(objectUnderTest.getAttribute(""));
        //Assert that we get no Attributes because we only provide access to public methods
        assertTrue(objectUnderTest.getAttributes(new String[0]).isEmpty());

        //Assert that we can not set any parameter
        var attribute = new Attribute("value", 42);
        assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttribute(attribute));
        //Assert that we can not set any parameter
        var attributeList = new AttributeList();
        assertThrows(UnsupportedOperationException.class,  () -> objectUnderTest.setAttributes(attributeList));
    }

}
