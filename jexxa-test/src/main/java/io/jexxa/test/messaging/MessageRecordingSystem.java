package io.jexxa.test.messaging;

import java.util.HashMap;
import java.util.Map;

public final class MessageRecordingSystem
{
    private static final MessageRecordingSystem MESSAGE_RECORDING_SYSTEM = new MessageRecordingSystem();

    private static final Map<Class<?>, MessageRecorder> MESSAGE_RECORDER_MAP = new HashMap<>();

    public MessageRecorder getMessageRecorder(Class<?> type)
    {
        //If MessageRecorder is not known for given type, we create it
        if ( !MESSAGE_RECORDER_MAP.containsKey(type) )
        {
            MESSAGE_RECORDER_MAP.put(type, new MessageRecorder());
        }

        return MESSAGE_RECORDER_MAP.get(type);
    }

    public static MessageRecordingSystem getInstance()
    {
        return MESSAGE_RECORDING_SYSTEM;
    }

    public static void reset()
    {
        MESSAGE_RECORDER_MAP.clear();
    }

    private MessageRecordingSystem()
    {
        //Private constructor
    }
}
