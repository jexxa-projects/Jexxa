package io.jexxa.test.messaging;

import java.util.ArrayList;
import java.util.List;

import io.jexxa.utils.annotations.CheckReturnValue;


public class MessageRecorder
{
    private final List<RecordedMessage> recordedMessageList= new ArrayList<>();


    void putMessage(RecordedMessage recordedMessage)
    {
        recordedMessageList.add(recordedMessage);
    }

    @CheckReturnValue
    public List<RecordedMessage> getMessages()
    {
        return recordedMessageList;
    }

    @CheckReturnValue
    public RecordedMessage pop()
    {
        if (recordedMessageList.isEmpty())
        {
            return null;
        }

        var latestElement = recordedMessageList.get(0);
        recordedMessageList.remove(0);
        return latestElement;
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
        return classType.cast(pop());
    }

}
