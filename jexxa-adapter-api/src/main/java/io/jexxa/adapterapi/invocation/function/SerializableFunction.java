package io.jexxa.adapterapi.invocation.function;

import java.io.Serializable;
import java.util.function.Function;

public interface SerializableFunction<T,R> extends Function<T,R>, Serializable { }