package io.jexxa.test.messaging;

import java.util.ArrayList;
import java.util.List;


public class MessageRecorder
{
    private final List<RecordedMessage> recordedMessageList= new ArrayList<>();


    void putMessage(RecordedMessage recordedMessage)
    {
        recordedMessageList.add(recordedMessage);
    }

    public List<RecordedMessage> getMessages()
    {
        return recordedMessageList;
    }

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
}
