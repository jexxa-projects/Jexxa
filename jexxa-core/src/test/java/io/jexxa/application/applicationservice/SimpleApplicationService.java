package io.jexxa.application.applicationservice;


import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.domain.model.JexxaRecord;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.application.domain.model.SpecialCasesValueObject;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
@ValidApplicationService
public class SimpleApplicationService
{
    private int firstValue;
    private List<String> messages = new ArrayList<>();
    private List<JexxaValueObject> valueObjects = new ArrayList<>();

    public static class SimpleApplicationException extends Exception
    {
        @Serial
        private static final long serialVersionUID = 1L;

        public SimpleApplicationException(String information)
        {
            super(information);
        }
        public SimpleApplicationException(String information, Throwable cause)
        {
            super(information, cause);
        }
    }

    public SimpleApplicationService()
    {
        firstValue = 42;
    }

    public int getSimpleValue()
    {
      return firstValue;
    }

    public int setGetSimpleValue(int newValue )
    {
        int oldValue = firstValue;
        this.firstValue = newValue;
        return oldValue;
    }

    public void throwExceptionTest() throws SimpleApplicationException
    {
        throw new SimpleApplicationException("TestException");
    }

    public int throwNullPointerException()   // Test runtime exception
    {
        JexxaValueObject jexxaValueObject = null;
        //noinspection ConstantConditions
        return jexxaValueObject.getValue();
    }


    public void setSimpleValue(int simpleValue)
    {
        this.firstValue = simpleValue;
    }

    public void setSimpleValueObject(JexxaValueObject simpleValueObject)
    {
        setSimpleValue(simpleValueObject.getValue());
    }

    public void setSimpleValueObjectTwice(JexxaValueObject first, JexxaValueObject second)
    {
        setSimpleValue(first.getValue());
        setSimpleValue(second.getValue());
    }

    public void addMessage(String message)
    {
        messages.add(message);
    }

    public void setMessages(List<String> messages)
    {
        this.messages = messages;
    }

    public void setValueObjectsAndMessages(List<JexxaValueObject> valueObjects, List<String> messages)
    {
        this.messages = messages;
        this.valueObjects = valueObjects;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    public List<JexxaValueObject> getValueObjects()
    {
        return valueObjects;
    }

    public JexxaValueObject getSimpleValueObject()
    {
        return  new JexxaValueObject(firstValue);
    }

    public SpecialCasesValueObject getSpecialCasesValueObject()
    {
        return  SpecialCasesValueObject.SPECIAL_CASES_VALUE_OBJECT;
    }

    public JexxaRecord getJexxaRecord()
    {
        return  new JexxaRecord(new String[0]);
    }

    /** The following static methods should NOT be offered by any DrivingAdapter according to our conventions  */
    public static SpecialCasesValueObject testStaticGetMethod()
    {
        throw new IllegalArgumentException("Method testStaticGetMethod should not be available or called" );
    }

    public static void testStaticSetMethod(JexxaValueObject jexxaValueObject)
    {
        throw new IllegalArgumentException("Method testStaticSetMethod should not be available or called" );
    }


}
