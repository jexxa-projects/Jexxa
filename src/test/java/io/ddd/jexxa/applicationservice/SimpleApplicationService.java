package io.ddd.jexxa.applicationservice;


import io.ddd.jexxa.domain.valueobject.SimpleValueObject;
import io.ddd.stereotype.applicationcore.ApplicationService;

@ApplicationService
public class SimpleApplicationService
{
    public class SimpleApplicationException extends Exception
    {
        public SimpleApplicationException(String information)
        {
            super(information);
        }
    }

    private int firstValue;

    public SimpleApplicationService() {
        this(42);
    }

    public SimpleApplicationService(int firstValue) {
        this.firstValue = firstValue;
    }
    
    public int getSimpleValue()
    {
      return firstValue;
    }

    public int setGetSimpleValue( int newValue )
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

    public void setSimpleValueObject(SimpleValueObject simpleValueObject)
    {
        setSimpleValue(simpleValueObject.getValue());
    }

    public void setSimpleValueObjectTwice(SimpleValueObject first, SimpleValueObject second)
    {
        setSimpleValue(first.getValue());
        setSimpleValue(second.getValue());
    }
    

    public SimpleValueObject getSimpleValueObject()
    {
        return  new SimpleValueObject(firstValue);
    }

}
