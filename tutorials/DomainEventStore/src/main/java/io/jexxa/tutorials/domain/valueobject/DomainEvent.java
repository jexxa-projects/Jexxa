package io.jexxa.tutorials.domain.valueobject;

import java.time.Instant;

import io.jexxa.addend.applicationcore.ValueObject;

@ValueObject
public class DomainEvent
{
    private final String uuid;
    private final String payloadType;
    private final String payload;
    private final Instant publishedAt;

    public DomainEvent(String uuid, String payloadType, String payload, Instant publishedAt )
    {
        this.uuid = uuid;
        this.payload = payload;
        this.payloadType = payloadType;
        this.publishedAt = publishedAt;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getPayload()
    {
        return payload;
    }

    public Instant getPublishedAt()
    {
        return publishedAt;
    }

    public String getPayloadType()
    {
        return payloadType;
    }
}
