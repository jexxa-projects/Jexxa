package io.jexxa.pattern.outbox;

import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.pattern.MessageSenderManager;
import io.jexxa.pattern.messaging.MessageProducer;
import io.jexxa.pattern.messaging.MessageSender;
import io.jexxa.pattern.messaging.jms.JMSSender;
import io.jexxa.pattern.persistence.repository.IRepository;
import io.jexxa.pattern.RepositoryManager;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;

/**
 * This class implements the  <a href="https://microservices.io/patterns/data/transactional-outbox.html">transactional outbox pattern</a>.
 * This class encapsulates both parts, storing messages to a database within the transaction of the incoming method call
 * and the message relay part.
 * <br>
 * In the current implementation, we check each 300 ms if a new message is available that is then forwarded.
 */
@SuppressWarnings("unused")
public class TransactionalOutboxSender extends MessageSender {
    private static TransactionalOutboxSender transactionalOutboxSender;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final IRepository<JexxaOutboxMessage, UUID> outboxRepository;
    private final MessageSender messageSender;


    public static MessageSender createInstance(Properties properties)
    {
        if (transactionalOutboxSender == null)
        {
            transactionalOutboxSender = new TransactionalOutboxSender(properties);
        }
        return transactionalOutboxSender;
    }

    private TransactionalOutboxSender(Properties properties)
    {
        this.outboxRepository = RepositoryManager
                .getRepository(JexxaOutboxMessage.class
                        , JexxaOutboxMessage::messageId
                        , properties );

        MessageSenderManager.setStrategy(JMSSender.class, TransactionalOutboxSender.class); // Ensure that we get a JMSSender for internal sending
        this.messageSender = MessageSenderManager.getMessageSender(TransactionalOutboxSender.class, properties);

        executor.schedule( this::transactionalSend, 300, TimeUnit.MILLISECONDS);
        JexxaContext.registerCleanupHandler(TransactionalOutboxSender::cleanup);
    }

    public static void cleanup()
    {
        if (transactionalOutboxSender != null){
            transactionalOutboxSender.internalCleanup();
            transactionalOutboxSender = null;
        }
    }

    void internalCleanup() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                getLogger(this.getClass()).warn("Could not successfully stop running operations -> Force shutdown");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            getLogger(TransactionalOutboxSender.class).warn("ExecutorService could not be stopped -> Interrupt thread.", e);
            Thread.currentThread().interrupt();
        }

        if (messageSender instanceof AutoCloseable autoCloseable)
        {
            try {
                autoCloseable.close();
            } catch (Exception e)
            {
                getLogger(TransactionalOutboxSender.class).error(e.getMessage());
            }
        }
    }

    /**
     * This method is the entry point for the message relay part of the transactional outbox pattern.
     * It calls method {@link #sendOutboxMessages()} in a transaction managed by the invocation manager
     */
    @SuppressWarnings("java:S1181")
    public void transactionalSend()
    {
        try {
            var handler = InvocationManager.getInvocationHandler(this);
            handler.invoke(this, this::sendOutboxMessages);
        } catch (InvocationTargetRuntimeException e)
        {
            getLogger(getClass()).warn("Could not send outbox messages. Reason: {}", e.getTargetException().getMessage());
        } catch (Throwable e)
        {
            getLogger(getClass()).error("{} occurred in transactionalSend. Reason: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new JexxaOutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.QUEUE));
        executor.schedule( this::transactionalSend,0, TimeUnit.MICROSECONDS);
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType) {
        outboxRepository.add(new JexxaOutboxMessage(
                UUID.randomUUID(), message,
                destination, messageProperties,
                messageType, DestinationType.TOPIC));
        executor.schedule( this::transactionalSend,0, TimeUnit.MICROSECONDS);
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
