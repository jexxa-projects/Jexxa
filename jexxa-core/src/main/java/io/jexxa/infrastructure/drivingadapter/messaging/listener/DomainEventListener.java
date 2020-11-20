package io.jexxa.infrastructure.drivingadapter.messaging.listener;

public abstract class DomainEventListener<T> extends TypedMessageListener<PublishedDomainEvent>
{
    private PublishedDomainEvent currentDomainEvent;
    private final Class<T> clazz;

    protected DomainEventListener(Class<T> clazz)
    {
        super(PublishedDomainEvent.class);
        this.clazz = clazz;
    }

    @Override
    public final void onMessage(PublishedDomainEvent message)
    {
        this.currentDomainEvent = message;

        onDomainEvent( getGson().fromJson(message.getPayload(), clazz) );

        this.currentDomainEvent = null;
    }

    protected PublishedDomainEvent getDomainEvent()
    {
        return currentDomainEvent;
    }

    public abstract void onDomainEvent(T domainEvent );
}
