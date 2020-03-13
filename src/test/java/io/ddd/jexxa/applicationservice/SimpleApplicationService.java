package io.ddd.jexxa.applicationservice;


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
}
