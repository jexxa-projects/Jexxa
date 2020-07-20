package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;

public final class MessageSenderManager
{
    private static final MessageSenderManager MESSAGE_SENDER_MANAGER = new MessageSenderManager();

    private MessageSenderManager()
    {
        //Private constructor
    }

    public MessageSender getStrategy(Properties properties)
    {
        return new JMSSender(properties);
    }

    public static MessageSenderManager getInstance()
    {
        return MESSAGE_SENDER_MANAGER;
    }
}
