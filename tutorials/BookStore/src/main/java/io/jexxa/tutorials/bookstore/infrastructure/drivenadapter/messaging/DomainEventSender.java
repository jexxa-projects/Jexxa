package io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookOutOfPrint;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;

@SuppressWarnings("unused")
public class DomainEventSender implements IDomainEventPublisher
{
    private final JMSSender jmsSender;
    private static final String BOOKSTORE_TOPIC = "BookStoreTopic";

    public DomainEventSender(Properties properties)
    {
        jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(BookOutOfPrint domainEvent)
    {
        jmsSender.sendToTopic(domainEvent, BOOKSTORE_TOPIC);
    }

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        jmsSender.sendToTopic(domainEvent, BOOKSTORE_TOPIC);
    }
}
