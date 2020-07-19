package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class JMSQueue
{
    private final String destination;

    JMSQueue(String destination)
    {
        this.destination = destination;
    }

    String getDestination()
    {
        return destination;
    }

    static JMSQueue of(String destination)
    {
        return new JMSQueue(destination);
    }
}
