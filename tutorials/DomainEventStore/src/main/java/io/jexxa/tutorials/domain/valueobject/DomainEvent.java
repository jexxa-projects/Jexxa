package io.jexxa.tutorials.domain.valueobject;

import java.time.Instant;

import io.jexxa.addend.applicationcore.ValueObject;

@ValueObject
public class DomainEvent
{
    private final String id;
    private final String type;
    private final String payload;
    private final Instant publishedAt;

    public DomainEvent(String id, String type, String payload, Instant publishedAt )
    {
        this.id = id;
        this.payload = payload;
        this.type = type;
        this.publishedAt = publishedAt;
    }

    public String getId()
    {
        return id;
    }

    public String getPayload()
    {
        return payload;
    }

    public Instant getPublishedAt()
    {
        return publishedAt;
    }

    public String getType()
    {
        return type;
    }
}
