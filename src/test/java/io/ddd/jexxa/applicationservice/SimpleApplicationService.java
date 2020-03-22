package io.ddd.jexxa.applicationservice;


import javax.lang.model.util.SimpleAnnotationValueVisitor9;

import io.ddd.jexxa.domain.valueobject.SimpleValueObject;
import io.ddd.stereotype.applicationcore.ApplicationService;

@ApplicationService
public class SimpleApplicationService
{
    public class SimpleApplicationExpcetion extends Exception
    {
        public SimpleApplicationExpcetion(String information)
        {
            super(information);
        }
    }

    private int firstValue;

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

    public void throwExcptionTest() throws SimpleApplicationExpcetion
    {
        throw new SimpleApplicationExpcetion("TestException");
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
