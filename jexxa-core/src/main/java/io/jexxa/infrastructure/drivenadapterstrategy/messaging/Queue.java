package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class Queue
{
    private final String destination;

    private Queue(String destination)
    {
        this.destination = destination;
    }

    public String getDestination()
    {
        return destination;
    }
    
    public static Queue queueOf(String destination)
    {
        return new Queue(destination);
    }
}
