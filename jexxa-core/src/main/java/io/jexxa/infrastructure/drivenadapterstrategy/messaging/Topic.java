package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class Topic
{
    private final String destination;

    private Topic(String destination)
    {
        this.destination = destination;
    }

    public String getDestination()
    {
        return destination;
    }

    public static Topic topicOf(String destination)
    {
        return new Topic(destination);
    }
}
