package io.jexxa.infrastructure.drivingadapter.messaging.listener;

public abstract class DomainEventListener<T> extends MessageContainerListener<DomainEventContainer>
{
    private final Class<T> payloadClazz;

    protected DomainEventListener(Class<T> payloadClazz)
    {
        super(DomainEventContainer.class, DomainEventContainer::getPayload);
        this.payloadClazz = payloadClazz;
    }

    protected void onMessageContainer(DomainEventContainer domainEventContainer )
    {
        onDomainEvent( fromJson(domainEventContainer.getPayload(), payloadClazz) );
    }

    protected abstract void onDomainEvent(T domainEvent);

}
