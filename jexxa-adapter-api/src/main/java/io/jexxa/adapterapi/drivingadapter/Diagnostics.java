package io.jexxa.adapterapi.drivingadapter;

import java.time.Instant;

@SuppressWarnings("unused")
public class Diagnostics
{
    private final boolean isHealthy;
    private final String statusMessage;
    private final String nameOfHealthCheck;
    private final String nameOfObservedObject;
    private final Instant timestamp;

    public Diagnostics(String nameOfHealthCheck, String nameOfObservedObject, boolean isHealthy, String statusMessage, Instant timestamp)
    {
        this.nameOfHealthCheck = nameOfHealthCheck;
        this.isHealthy = isHealthy;
        this.statusMessage = statusMessage;
        this.nameOfObservedObject = nameOfObservedObject;
        this.timestamp = timestamp;
    }

    public String getNameOfHealthCheck() {
        return nameOfHealthCheck;
    }

    public String getNameOfObservedObject() {
        return nameOfObservedObject;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }
}