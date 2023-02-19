package io.jexxa.infrastructure.drivenadapterstrategy.outbox;

import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnectionPool;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.infrastructure.drivingadapter.scheduler.Scheduler;
import io.jexxa.utils.JexxaLogger;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the  <a href="https://microservices.io/patterns/data/transactional-outbox.html">transactional outbox pattern</a>.
 * This class encapsulates both parts, storing messages to a database within the transaction of the incoming method call
 * and the message relay part.
 * <br>
 * In the current implementation, we check each 300 ms if a new message is available that is then forwarded.
 */
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

        JDBCConnectionPool.configureExclusiveConnection(outboxRepository, JDBCConnection.IsolationLevel.SERIALIZABLE);

        executor.scheduleAtFixedRate( this::transactionalSend, 300, 300, TimeUnit.MILLISECONDS);
        JexxaContext.registerCleanupHandler(this::cleanup);
    }

    public void cleanup() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            JexxaLogger.getLogger(Scheduler.class).warn("ExecutorService could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method is the entry point for the message relay part of the transactional outbox pattern.
     * It calls method {@link #sendOutboxMessages()} in a transaction managed by the invocation manager
     */
    public void transactionalSend()
    {
        try {
            InvocationManager.getInvocationHandler(this).invoke(this, this::sendOutboxMessages);
        } catch (InvocationTargetRuntimeException e)
        {
            JexxaLogger.getLogger(getClass()).warn("Could not send outbox messages. Reason: {}", e.getTargetException().getMessage());
        } catch (Throwable e)
        {
            JexxaLogger.getLogger(getClass()).error("{} occurred in transactionalSend occurred. Reason: {}", e.getClass().getSimpleName(), e.getMessage());
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
        producer.addHeader("domain_event_id", outboxMessage.messageId().toString()).asString();
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
        producer.addHeader("domain_event_id", outboxMessage.messageId().toString()).asString();
    }


    enum DestinationType{ TOPIC, QUEUE }

    record JexxaOutboxMessage(UUID messageId, String message, String destination,
                              Properties messageProperties, MessageType messageType,
                              DestinationType destinationType)
    {   }

}
