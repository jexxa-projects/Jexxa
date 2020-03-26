package io.ddd.jexxa.applicationcore.applicationservice;

import io.ddd.jexxa.applicationcore.domain.valueobject.JexxaValueObject;

public class UnsupportedApplicationService
{
    private JexxaValueObject first;
    private JexxaValueObject second;

    public void setSimpleValueObject(JexxaValueObject simpleValueObject)
    {
        this.first = simpleValueObject;
    }
    public void setSimpleValueObject(JexxaValueObject first, JexxaValueObject second)
    {
        this.first = first;
        this.second = second;
    }
}
