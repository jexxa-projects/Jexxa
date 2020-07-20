package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.function.Function;
import java.util.function.Supplier;

public interface JMessage
{
    JMessage to(JQueue queue);

    JMessage to(JTopic topic);

    JMessage addHeader(String key, String value);

    void asJson();

    void asString();

    void as( Function<Object, String> serializer );

    void as( Supplier<String> serializer );
}
