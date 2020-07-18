package io.jexxa.tutorial.bookstorej.infrastructure.drivenadapter.console;

import io.jexxa.tutorial.bookstorej.domain.domainevent.BookSoldOut;
import io.jexxa.tutorial.bookstorej.domainservice.IDomainEventPublisher;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class ConsolePublisher implements IDomainEventPublisher
{
    private static final Logger LOGGER = JexxaLogger.getLogger(ConsolePublisher.class);

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        Validate.notNull(domainEvent);

        var logMessage = domainEvent.getClass().getSimpleName() + ": " + domainEvent.getISBN13().getValue();

        LOGGER.info(logMessage);
    }
}
