package io.jexxa.infrastructure.drivingadapter.rest;

import io.jexxa.application.domain.valueobject.JexxaValueObject;

/*
* This service is not available via RESTfulRPC because method setSimpleValueObject is available twice 
*/
@SuppressWarnings("unused")
class UnsupportedApplicationService
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
