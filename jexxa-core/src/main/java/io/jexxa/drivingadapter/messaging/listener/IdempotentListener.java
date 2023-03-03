package io.jexxa.drivingadapter.messaging.listener;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.jexxa.api.wrapper.logger.SLF4jLogger.getLogger;

public abstract class IdempotentListener<T> extends JSONMessageListener
{
    private static final String DEFAULT_MESSAGE_ID = "domain_event_id";
    List<String> processedMessages = new ArrayList<>();
    private final Class<T> clazz;

    protected IdempotentListener(Class<T> clazz)
    {
        this.clazz = Objects.requireNonNull( clazz );
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
        String messageID = getMessageHeaderValue(uniqueID);
        if (processedMessages.contains(messageID)) {
            getLogger(getClass()).info("Message with key {} already processed -> Ignore it", messageID);
            return;
        }

        onMessage( fromJson(message, clazz ));
        processedMessages.add(messageID);
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
}
