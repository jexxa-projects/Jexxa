package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import java.time.Instant;

@SuppressWarnings("unused")
public
class DomainEventFrame
{
    private final String uuid;
    private final String payloadType;
    private final String payload;
    private final Instant publishedAt;

    DomainEventFrame(String uuid, String payloadType, String payload, Instant publishedAt )
    {
        this.uuid = uuid;
        this.payloadType = payloadType;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getType()
    {
        return payloadType;
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
