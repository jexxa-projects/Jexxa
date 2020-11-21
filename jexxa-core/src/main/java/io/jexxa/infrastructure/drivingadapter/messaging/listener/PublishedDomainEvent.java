package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import java.time.Instant;

@SuppressWarnings("unused")
public class PublishedDomainEvent
{
    private final String id;
    private final String type;
    private final String payload;
    private final Instant publishedAt;

    public PublishedDomainEvent(String id, String type, String payload, Instant publishedAt )
    {
        this.id = id;
        this.payload = payload;
        this.type = type;
        this.publishedAt = publishedAt;
    }

    public String getUUID()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getPayload()
    {
        return payload;
    }

    public Instant getPublishedAt()
    {
        return publishedAt;
    }
}
