package io.jexxa.infrastructure.drivenadapterstrategy.outbox;

import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.utils.JexxaLogger;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class TransactionalOutboxSender extends MessageSender {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final IRepository<JexxaOutboxMessage, UUID> outboxRepository;
    private final Properties properties;

    public TransactionalOutboxSender(Properties properties)
    {
        MessageSenderManager.setStrategy(JMSSender.class, TransactionalOutboxSender.class); // Ensure that we get a JMSSender for internal sending
        this.properties = properties;
        this.outboxRepository = RepositoryManager
                .getRepository(JexxaOutboxMessage.class
                        , JexxaOutboxMessage::messageId
                        , properties );

        executor.scheduleAtFixedRate( this::transactionalSend, 500, 500, TimeUnit.MILLISECONDS);
        JexxaContext.registerCleanupHandler(this::cleanup);
    }

    public void cleanup() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            executor.shutdownNow();
        }
    }

    public void transactionalSend()
    {
        try {
            InvocationManager.getInvocationHandler(this).invoke(this, this::sendOutboxMessages);
        } catch (InvocationTargetRuntimeException e)
        {
            JexxaLogger.getLogger(getClass()).warn("Could not send outbox messages. Reason: {}", e.getTargetException().getMessage());
        }
    }

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new JexxaOutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.QUEUE));
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new JexxaOutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.TOPIC));
    }

    private void sendOutboxMessages()
    {
        outboxRepository.get().stream()
                .filter(outboxMessage -> outboxMessage.destinationType.equals(DestinationType.QUEUE))
                .forEach(this::sendToQueue);

        outboxRepository.get().stream()
                .filter(outboxMessage -> outboxMessage.destinationType.equals(DestinationType.TOPIC))
                .forEach(this::sendToTopic);

        outboxRepository.removeAll();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sendToQueue(JexxaOutboxMessage outboxMessage)
    {
        var messageSender = MessageSenderManager.getMessageSender(TransactionalOutboxSender.class, properties);
        MessageProducer producer;
        if (outboxMessage.messageType.equals(MessageType.TEXT_MESSAGE))
        {
            producer = messageSender.send(outboxMessage.message()).toQueue(outboxMessage.destination());
        } else {
            producer = messageSender.sendByteMessage(outboxMessage.message()).toQueue(outboxMessage.destination());
        }
        if (outboxMessage.messageProperties() != null) {
            outboxMessage.messageProperties().forEach((key, value) -> producer.addHeader((String) key, (String) value));
        }
        producer.asString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sendToTopic( JexxaOutboxMessage outboxMessage)
    {
        var messageSender = MessageSenderManager.getMessageSender(TransactionalOutboxSender.class, properties);
        MessageProducer producer;
        if (outboxMessage.messageType().equals(MessageType.TEXT_MESSAGE))
        {
            producer = messageSender.send(outboxMessage.message()).toTopic(outboxMessage.destination());
        } else {
            producer = messageSender.sendByteMessage(outboxMessage.message()).toTopic(outboxMessage.destination());
        }
        if (outboxMessage.messageProperties() != null) {
            outboxMessage.messageProperties().forEach((key, value) -> producer.addHeader((String) key, (String) value));
        }
        producer.asString();
    }


    enum DestinationType{ TOPIC, QUEUE }

    record JexxaOutboxMessage(UUID messageId, String message, String destination,
                              Properties messageProperties, MessageType messageType,
                              DestinationType destinationType)
    {   }
}
