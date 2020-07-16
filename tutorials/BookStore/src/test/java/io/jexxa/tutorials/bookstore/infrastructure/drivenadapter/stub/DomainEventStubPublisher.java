package io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub;

import java.util.ArrayList;
import java.util.List;

import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;

@SuppressWarnings("unused")
public class DomainEventStubPublisher implements IDomainEventPublisher
{
    private static final List<Object> EVENT_LIST = new ArrayList<>();

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        EVENT_LIST.add(domainEvent);
    }

    public static int eventCount()
    {
        return EVENT_LIST.size();
    }

    public static Object getEvent(int position)
    {
        return EVENT_LIST.get(position);
    }

    public static void clear()
    {
        EVENT_LIST.clear();
    }
}
