package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public interface MessageSender
{
    <T> JMessage send(T message);

    
    <T> void sendToTopic(T message, String topicName);

    <T> void sendToQueue(T message, String queueName);
}
