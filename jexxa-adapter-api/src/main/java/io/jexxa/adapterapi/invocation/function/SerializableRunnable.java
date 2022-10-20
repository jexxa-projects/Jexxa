package io.jexxa.adapterapi.invocation.function;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableRunnable extends Runnable, Serializable
{
    // Functional interface for a runnable that can be serialized
}
