package io.jexxa.jexxatest.infrastructure.messaging.recording;

import io.jexxa.common.annotation.CheckReturnValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is the API for unit test to access and validate recorded messages.
 */
public class MessageRecorder
{
    private final List<RecordedMessage> recordedMessageList = new ArrayList<>();

    @CheckReturnValue
    @SuppressWarnings("unused")
    public List<RecordedMessage> getMessages()
    {
        return recordedMessageList;
    }

    @CheckReturnValue
    public Optional<RecordedMessage> pop()
    {
        if (recordedMessageList.isEmpty())
        {
            return Optional.empty();
        }

        var latestElement = recordedMessageList.get(0);
        recordedMessageList.remove(0);
        return Optional.ofNullable(latestElement);
    }

    @CheckReturnValue
    public boolean isEmpty()
    {
        return recordedMessageList.isEmpty();
    }

    @CheckReturnValue
    public int size()
    {
        return recordedMessageList.size();
    }

    @CheckReturnValue
    public <T> T getMessage(Class<T> classType)
    {
        return  pop().orElseThrow().getMessage(classType);
    }

    public void clear()
    {
        recordedMessageList.clear();
    }

    void put(RecordedMessage recordedMessage)
    {
        recordedMessageList.add(recordedMessage);
    }

}
