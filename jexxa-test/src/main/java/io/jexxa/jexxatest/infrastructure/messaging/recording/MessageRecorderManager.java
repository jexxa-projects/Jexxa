package io.jexxa.jexxatest.infrastructure.messaging.recording;

import io.jexxa.common.facade.utils.annotation.CheckReturnValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton, which manages all instances of MessageRecorder
 */
public final class MessageRecorderManager
{
    private static final Map<Class<?>, MessageRecorder> MESSAGE_RECORDER_MAP = new HashMap<>();

    @CheckReturnValue
    public static MessageRecorder getMessageRecorder(Class<?> type)
    {
        //If MessageRecorder is not known for a given type, we create it
        return MESSAGE_RECORDER_MAP.computeIfAbsent(type, element -> new MessageRecorder());
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
