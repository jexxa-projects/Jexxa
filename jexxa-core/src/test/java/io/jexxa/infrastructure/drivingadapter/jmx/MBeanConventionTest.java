package io.jexxa.infrastructure.drivingadapter.jmx;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.management.Attribute;
import javax.management.AttributeList;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;
import static io.jexxa.utils.properties.JexxaCoreProperties.JEXXA_CONTEXT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        assertEquals(properties.get(JEXXA_CONTEXT_NAME) + ":type=ApplicationService,name=SimpleApplicationService",
                objectUnderTest.getDomainPath());
    }

    @Test
    void toJsonTemplatePrimitive()
    {
        //Arrange
        String integerTemplate = "<int>";
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

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
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

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
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

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
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

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
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValue";
        var newValue = 5;

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{getJSONConverter().toJson(newValue)}, new String[0]);

        //Assert
        assertEquals(newValue, applicationService.getSimpleValue());
    }


    @Test
    void invokeSetSimpleValueObject()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var action = "setSimpleValueObject";
        var newValue = new JexxaValueObject(5);

        var objectUnderTest = new MBeanConvention(applicationService, properties);

        //Act
        objectUnderTest.invoke(action, new String[]{getJSONConverter().toJson(newValue)}, new String[0]);

        //Assert
        assertEquals(newValue.getValue(), applicationService.getSimpleValueObject().getValue());
    }


    @Test
    void disabledMBeanModelMethods()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());

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

    @Test
    void noStaticMethods()
    {
        //Arrange
        var applicationService = new SimpleApplicationService();
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, getClass().getSimpleName());
        var staticMethods = Arrays.stream(applicationService.getClass().getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers())).toList();

        //Act
        var objectUnderTest = new MBeanConvention(applicationService, properties);
        var mbeanMethods = objectUnderTest.getMBeanInfo().getOperations();

        //Assert - that we get mbean methods without static methods
        assertNotNull(mbeanMethods);
        assertTrue ( staticMethods.stream().allMatch(
                staticMethod -> Arrays.stream(mbeanMethods)
                        .noneMatch( mbeanInfo -> mbeanInfo.getName().equals(staticMethod.getName()))
                )
        );
    }

}
