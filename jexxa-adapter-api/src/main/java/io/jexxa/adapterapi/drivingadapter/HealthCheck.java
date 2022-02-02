package io.jexxa.adapterapi.drivingadapter;

import java.time.Instant;

public abstract class HealthCheck
{
    private Object observedObject;

    public abstract boolean healthy();

    public abstract String getStatusMessage();

    public Diagnostics getDiagnostics()
    {
        if ( observedObject != null )
        {
            return new Diagnostics(getClass().getSimpleName(), observedObject.getClass().getSimpleName(), healthy(), getStatusMessage(), Instant.now());
        }

        return new Diagnostics(getClass().getSimpleName(), "Unknown", healthy(), getStatusMessage(), Instant.now());
    }

    public void setObservedObject(Object observedObject)
    {
        this.observedObject = observedObject;
    }

    protected Object getObservedObject()
    {
        return observedObject;
    }
}
