package io.ddd.jexxa.application.applicationservice;


import io.ddd.jexxa.application.annotation.ApplicationService;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;

@SuppressWarnings("unused")
@ApplicationService
public class SimpleApplicationService
{
    public static class SimpleApplicationException extends Exception
    {
        public SimpleApplicationException(String information)
        {
            super(information);
        }
    }

    private int firstValue;

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
    

    public JexxaValueObject getSimpleValueObject()
    {
        return  new JexxaValueObject(firstValue);
    }

}
