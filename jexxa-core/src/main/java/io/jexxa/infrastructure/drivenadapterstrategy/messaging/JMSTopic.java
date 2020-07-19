package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

public class JMSTopic
{
    private final String destination;

    JMSTopic(String destination)
    {
        this.destination = destination;
    }

    String getDestination()
    {
        return destination;
    }

    static JMSTopic of(String destination)
    {
        return new JMSTopic(destination);
    }
}
