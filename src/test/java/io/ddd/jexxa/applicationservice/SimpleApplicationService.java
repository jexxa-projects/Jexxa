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

    public SimpleValueObject getSimpleValueObject()
    {
        return  new SimpleValueObject(simpleValue);
    }

}
