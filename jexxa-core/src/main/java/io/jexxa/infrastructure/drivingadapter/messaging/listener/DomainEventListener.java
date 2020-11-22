package io.jexxa.infrastructure.drivingadapter.messaging.listener;

public abstract class DomainEventListener<T> extends TypedMessageListener<DomainEventFrame>
{
    private DomainEventFrame currentDomainEvent;
    private final Class<T> clazz;

    protected DomainEventListener(Class<T> clazz)
    {
        super(DomainEventFrame.class);
        this.clazz = clazz;
    }

    @Override
    public final void onMessage(DomainEventFrame message)
    {
        this.currentDomainEvent = message;

        onDomainEvent( getGson().fromJson(message.getPayload(), clazz) );

        this.currentDomainEvent = null;
    }

    protected DomainEventFrame getDomainEvent()
    {
        return currentDomainEvent;
    }

    public abstract void onDomainEvent(T domainEvent );
}
