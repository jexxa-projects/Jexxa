package io.jexxa.adapterapi.invocation.function;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable
{
    // Functional interface for a consumer that can be serialized
}
