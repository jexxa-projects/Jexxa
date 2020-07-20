package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Message
{
    Message to(Queue queue);

    Message to(Topic topic);

    Message addHeader(String key, String value);
    Message withHeader(String key, String value);


    void asJson();

    void asString();

    void as( Function<Object, String> serializer );

    void as( Supplier<String> serializer );
}
