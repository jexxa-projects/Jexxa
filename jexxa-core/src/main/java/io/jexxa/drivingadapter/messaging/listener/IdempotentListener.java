package io.jexxa.drivingadapter.messaging.listener;

import io.jexxa.infrastructure.RepositoryManager;
import io.jexxa.infrastructure.persistence.repository.IRepository;

import javax.jms.JMSException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;

public abstract class IdempotentListener<T> extends JSONMessageListener
{
    private static final Duration DEFAULT_STORAGE_DURATION = Duration.ofDays(7);
    private static final String DEFAULT_MESSAGE_ID = "domain_event_id";
    private final IRepository<JexxaInboundMessage, ReceivingID> messageRepository;
    private final Class<T> clazz;
    private Instant lastCleanupTime;

    protected IdempotentListener(Class<T> clazz, Properties properties)
    {
        this.clazz = Objects.requireNonNull( clazz );
        messageRepository = RepositoryManager.getRepository(JexxaInboundMessage.class, JexxaInboundMessage::receivingID, properties);
    }
    @Override
    public final void onMessage(String message)
    {
        // If we do not find a uniqueID, we show a warning and process the message
        var uniqueID = uniqueID();
        if ( !messageHeaderIncludes( uniqueID ))
        {
            getLogger(getClass()).warn("Message does not include an ID {} -> Process message", uniqueID);
            onMessage( fromJson(message, clazz ));
            return;
        }

        // If we already processed the ID, we show an info message and return
        var receivingID = new ReceivingID(getMessageHeaderValue(uniqueID), this.getClass().getName());
        if (messageRepository.get(receivingID).isPresent()) {
            getLogger(getClass()).info("Message with key {} already processed by {} -> Ignore it", receivingID.uuid, receivingID.className);
            return;
        }

        onMessage( fromJson(message, clazz ));
        messageRepository.add(new JexxaInboundMessage(receivingID, Instant.now()));
        removeOldMessages();
    }
    public abstract void onMessage(T message);

    protected String uniqueID()
    {
        return DEFAULT_MESSAGE_ID;
    }

    protected boolean messageHeaderIncludes(String key)
    {
        try {
            if (getCurrentMessage() != null) {
                return getCurrentMessage().propertyExists(key);
            } else {
                return false;
            }
        } catch (JMSException e)
        {
            return false;
        }
    }

    protected Duration getStorageDuration()
    {
        return DEFAULT_STORAGE_DURATION;
    }

    protected String getMessageHeaderValue(String key)
    {
        try {
            if (getCurrentMessage() != null) {
                return getCurrentMessage().getStringProperty(key);
            }
        } catch (JMSException e)
        {
            return null;
        }
        return null;
    }

    private void removeOldMessages()
    {
        //Remove old messages once per day
        if (lastCleanupTime != null) {
            var durationSinceLastCheck = Duration.between(lastCleanupTime, Instant.now() );
            if (durationSinceLastCheck.compareTo(Duration.ofDays(1))<0) {
                return;
            }
        }

        messageRepository.get().stream()
                .filter(element -> Duration.between( element.processingTime, Instant.now())
                        .compareTo(getStorageDuration()) >= 0)
                .forEach(element -> messageRepository.remove(element.receivingID));

        lastCleanupTime = Instant.now();
    }

    record JexxaInboundMessage(ReceivingID receivingID, Instant processingTime){}
    record ReceivingID(String uuid, String className){}
}
