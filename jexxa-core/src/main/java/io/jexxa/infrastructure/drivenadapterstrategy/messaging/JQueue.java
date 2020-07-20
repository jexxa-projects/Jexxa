package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class JQueue
{
    private final String destination;

    private JQueue(String destination)
    {
        this.destination = destination;
    }

    public String getDestination()
    {
        return destination;
    }

    public static JQueue of(String destination)
    {
        return new JQueue(destination);
    }
}
