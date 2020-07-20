package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class JTopic
{
    private final String destination;

    private JTopic(String destination)
    {
        this.destination = destination;
    }

    public String getDestination()
    {
        return destination;
    }

    public static JTopic of(String destination)
    {
        return new JTopic(destination);
    }
}
