package io.jexxa.testapplication.applicationservice;


import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.testapplication.annotation.ValidApplicationService;
import io.jexxa.testapplication.domain.model.JexxaEnum;
import io.jexxa.testapplication.domain.model.JexxaRecord;
import io.jexxa.testapplication.domain.model.JexxaRecordComparable;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domain.model.SpecialCasesValueObject;

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
    private List<JexxaRecord> recordList = new ArrayList<>();

    private JexxaEnum jexxaEnum = JexxaEnum.ENUM_VALUE1;

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

    public void setEnumValue(JexxaEnum jexxaEnum)
    {
        this.jexxaEnum = jexxaEnum;
    }

    public JexxaEnum getEnumValue()
    {
        return jexxaEnum;
    }

    @SuppressWarnings("DataFlowIssue") // Because this method should caus a NullPointerException for testing purpose
    public int throwNullPointerException()   // Test runtime exception
    {
        JexxaValueObject jexxaValueObject = null;
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

    public void setSimpleValueTwice(int first, int second)
    {
        setSimpleValue(first);
        setSimpleValue(second);
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

    public List<JexxaRecordComparable> getJexxaRecordComparableList()
    {
        return List.of(new JexxaRecordComparable(42));
    }

    public JexxaRecordComparable getJexxaRecordComparable()
    {
        return  new JexxaRecordComparable(42);
    }

    public JexxaRecord getJexxaRecord()
    {
        return  new JexxaRecord("");
    }

    public List<JexxaRecord> getJexxaRecordList()
    {
        return  recordList;
    }

    public void setJexxaRecordList(List<JexxaRecord> recordList)
    {
        recordList.forEach(element -> SLF4jLogger.getLogger(SimpleApplicationService.class).info(element.jexxaRecord()));
        this.recordList = recordList;
    }

    /** Any DrivingAdapter should NOT offer the following static methods according to our conventions  */
    public static SpecialCasesValueObject testStaticGetMethod()
    {
        throw new IllegalArgumentException("Method testStaticGetMethod should not be available or called" );
    }

    public static void testStaticSetMethod(JexxaValueObject jexxaValueObject)
    {
        throw new IllegalArgumentException("Method testStaticSetMethod should not be available or called" );
    }


}
