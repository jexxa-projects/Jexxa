package io.jexxa.adapterapi.invocation.function;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableFunction<T,R> extends Function<T,R>, Serializable
{
    // Functional interface for a function that can be serialized
}
