package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public interface MessageSender
{
    <T> Message send(T message);

    
    <T> void sendToTopic(T message, String topicName);

    <T> void sendToQueue(T message, String queueName);
}
