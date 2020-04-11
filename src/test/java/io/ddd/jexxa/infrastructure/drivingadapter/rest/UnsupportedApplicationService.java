package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;

/*
* This service is not available via RESTfulRPC because method setSimpleValueObject is available twice 
*/
@SuppressWarnings("unused")
public class UnsupportedApplicationService
{
    private JexxaValueObject first;
    @SuppressWarnings("FieldCanBeLocal")
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
