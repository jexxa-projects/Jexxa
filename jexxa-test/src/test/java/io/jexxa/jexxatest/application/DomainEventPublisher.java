package io.jexxa.jexxatest.application;

import io.jexxa.addend.applicationcore.Observer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Observer
public final class DomainEventPublisher {
    private final Map<Class<?>, Set<Consumer<?>>> subscribers = new ConcurrentHashMap<>();
    private static final DomainEventPublisher DOMAIN_EVENT_PUBLISHER = new DomainEventPublisher();

    public static DomainEventPublisher instance()
    {
        return DOMAIN_EVENT_PUBLISHER;
    }

    @SuppressWarnings("unchecked") // We check if the given domainEvent is assignable to a listener. Therefore, the unchecked cast is safe
    public static synchronized <T> void publish(final T domainEvent)
    {
        instance()
                .subscribers
                .entrySet()
                .stream()
                .filter(element -> element.getKey().isAssignableFrom(domainEvent.getClass()))
                .flatMap(element -> element.getValue().stream())
                .forEach(element -> ((Consumer<T>) element).accept(domainEvent));
    }

    public static synchronized <T> void subscribe(Class<T> domainEvent, Consumer<T> subscriber)
    {
        instance().subscribers.putIfAbsent(domainEvent, new HashSet<>());
        instance().subscribers.get(domainEvent).add(subscriber);
    }

    public static synchronized void subscribe(Consumer<Object> subscriber)
    {
        subscribe(Object.class, subscriber);
    }

    private DomainEventPublisher()
    {
        //Private constructor
    }
}
