package io.ddd.jexxa.applicationservice;

import io.ddd.jexxa.domain.valueobject.SimpleValueObject;

public class UnsupportedApplicationService
{
    private SimpleValueObject first;
    private SimpleValueObject second;

    public void setSimpleValueObject(SimpleValueObject simpleValueObject)
    {
        this.first = simpleValueObject;
    }
    public void setSimpleValueObject(SimpleValueObject first, SimpleValueObject second)
    {
        this.first = first;
        this.second = second;
    }
}
