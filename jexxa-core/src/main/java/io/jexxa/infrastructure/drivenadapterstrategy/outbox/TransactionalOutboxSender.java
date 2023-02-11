package io.jexxa.infrastructure.drivenadapterstrategy.outbox;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;

import java.util.Properties;
import java.util.UUID;

@SuppressWarnings("unused")
public class TransactionalOutboxSender extends MessageSender {
    private final IRepository<OutboxMessage, UUID> outboxRepository;

    public TransactionalOutboxSender(Properties properties)
    {
        this.outboxRepository = RepositoryManager.getRepository(OutboxMessage.class
                , OutboxMessage::messageId, properties);
    }

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new OutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.QUEUE));
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new OutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.TOPIC));

    }

    enum DestinationType{
        TOPIC,
        QUEUE
    }

    record OutboxMessage(UUID messageId, String message, String destination,
                         Properties messageProperties, MessageType messageType,
                         DestinationType destinationType)
    {

    }
}
