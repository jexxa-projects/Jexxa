package io.ddd.jhexa.applicationcore;


import io.ddd.stereotype.applicationcore.ApplicationService;

@ApplicationService
public class SimpleApplicationService
{
    private int simpleValue;

    public SimpleApplicationService(int simpleValue) {
        this.simpleValue = simpleValue;
    }
    
    int getSimpleValue()
    {
      return  simpleValue;
    }
}
