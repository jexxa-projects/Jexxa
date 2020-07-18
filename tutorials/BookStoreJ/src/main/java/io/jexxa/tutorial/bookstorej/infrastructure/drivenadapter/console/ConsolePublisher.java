package io.jexxa.tutorial.bookstorej.infrastructure.drivenadapter.console;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.tutorial.bookstorej.domainservice.IDomainEventPublisher;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@DrivenAdapter
public class ConsolePublisher implements IDomainEventPublisher
{
    private static final Logger LOGGER = JexxaLogger.getLogger(ConsolePublisher.class);

    @Override
    public <T> void publish(T domainEvent)
    {
        Validate.notNull(domainEvent);

        //Validate that we get an DomainEvent
        Validate.notNull(domainEvent.getClass().getAnnotation(DomainEvent.class));

        var logMessage = domainEvent.toString();

        LOGGER.info(logMessage);
    }
}
