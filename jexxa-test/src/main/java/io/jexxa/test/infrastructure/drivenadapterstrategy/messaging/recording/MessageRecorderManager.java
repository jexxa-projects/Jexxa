package io.jexxa.test.infrastructure.drivenadapterstrategy.messaging.recording;

import java.util.HashMap;
import java.util.Map;

import io.jexxa.utils.annotations.CheckReturnValue;

/**
 * Singleton which manages all instances of MessageRecorder
 */
public final class MessageRecorderManager
{
    private static final MessageRecorderManager MESSAGE_RECORDER_MANAGER = new MessageRecorderManager();

    private static final Map<Class<?>, MessageRecorder> MESSAGE_RECORDER_MAP = new HashMap<>();

    @CheckReturnValue
    public MessageRecorder getMessageRecorder(Class<?> type)
    {
        //If MessageRecorder is not known for given type, we create it
        if ( !MESSAGE_RECORDER_MAP.containsKey(type) )
        {
            MESSAGE_RECORDER_MAP.put(type, new MessageRecorder());
        }

        return MESSAGE_RECORDER_MAP.get(type);
    }

    @CheckReturnValue
    public static MessageRecorderManager getInstance()
    {
        return MESSAGE_RECORDER_MANAGER;
    }

    public static void clear()
    {
        MESSAGE_RECORDER_MAP.forEach(( key, value) -> value.clear() );
        MESSAGE_RECORDER_MAP.clear();
    }

    private MessageRecorderManager()
    {
        //Private constructor
    }
}
