package io.jexxa.tutorials.domaineventstore.domain.domainevent;

import java.time.Instant;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.BatchNumber;

@DomainEvent
public class MyDomainEvent
{
    private final String uuid;
    private final BatchNumber batchNumber;
    private final Instant timestamp;

    public MyDomainEvent(String uuid, BatchNumber batchNumber, Instant timestamp)
    {
        this.uuid = uuid;
        this.batchNumber = batchNumber;
        this.timestamp = timestamp;
    }

    public String getUuid()
    {
        return uuid;
    }

    public BatchNumber getBatchNumber()
    {
        return batchNumber;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }
}
