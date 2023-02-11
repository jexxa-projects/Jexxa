package io.jexxa.infrastructure.drivenadapterstrategy.outbox;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
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
    private final IRepository<OutboxMessage, UUID> outboxRepository;
    private final OutboxJMSSender outboxJMSSender;

    public TransactionalOutboxSender(Properties properties)
    {
        this.outboxRepository = RepositoryManager
                .getRepository(OutboxMessage.class
                        , OutboxMessage::messageId
                        , properties
                );
        this.outboxJMSSender = new OutboxJMSSender(properties);
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

    private static class OutboxJMSSender
    {
        private final IRepository<OutboxMessage, UUID> outboxRepository;
        private final Properties properties;
        private final ScheduledExecutorService executor;

        public OutboxJMSSender(Properties properties)
        {
            this.properties = properties;
            this.outboxRepository = RepositoryManager
                    .getRepository(OutboxMessage.class
                            , OutboxMessage::messageId
                            , properties
                    );

            this.executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> { System.out.println("Send messages"); transactionalSend();}, 500, 500, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void finalize() {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    System.out.println("Closed Transactional Sender");
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
                JexxaLogger.getLogger(getClass()).warn("Coud not send outobx messages. Reason: {}", e.getTargetException().getMessage());
            }
        }

        public void sendOutboxMessages()
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
        void sendToQueue(OutboxMessage outboxMessage)
        {
            var messageSender = MessageSenderManager.getMessageSender(TransactionalOutboxSender.class, properties);
            var producer = messageSender.send(outboxMessage.message()).toQueue(outboxMessage.destination);
            outboxMessage.messageProperties.forEach((key, value) -> producer.addHeader((String)key, (String)value));
            producer.asString();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        void sendToTopic( OutboxMessage outboxMessage)
        {
            var messageSender = MessageSenderManager.getMessageSender(TransactionalOutboxSender.class, properties);
            var producer = messageSender.send(outboxMessage.message()).toTopic(outboxMessage.destination);
            outboxMessage.messageProperties.forEach((key, value) -> producer.addHeader((String)key, (String)value));
            producer.asString();
        }

    }
}
