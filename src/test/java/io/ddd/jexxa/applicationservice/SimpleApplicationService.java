package io.ddd.jexxa.applicationservice;


import io.ddd.jexxa.domain.valueobject.SimpleValueObject;
import io.ddd.stereotype.applicationcore.ApplicationService;

@ApplicationService
public class SimpleApplicationService
{
    private int simpleValue;

    public SimpleApplicationService(int simpleValue) {
        this.simpleValue = simpleValue;
    }
    
    public int getSimpleValue()
    {
      return  simpleValue;
    }

    public void setSimpleValue(int simpleValue)
    {
        this.simpleValue = simpleValue;
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
        return  new SimpleValueObject(simpleValue);
    }

}
