package io.jexxa.infrastructure.drivingadapter.messaging.listener;

public abstract class DomainEventListener<T> extends TypedMessageListener<DomainEventContainer>
{
    private DomainEventContainer currentDomainEvent;
    private final Class<T> clazz;

    protected DomainEventListener(Class<T> clazz)
    {
        super(DomainEventContainer.class);
        this.clazz = clazz;
    }

    @Override
    public final void onMessage(DomainEventContainer message)
    {
        this.currentDomainEvent = message;

        onDomainEvent( getGson().fromJson(message.getPayload(), clazz) );

        this.currentDomainEvent = null;
    }

    protected DomainEventContainer getDomainEvent()
    {
        return currentDomainEvent;
    }

    public abstract void onDomainEvent(T domainEvent );
}
