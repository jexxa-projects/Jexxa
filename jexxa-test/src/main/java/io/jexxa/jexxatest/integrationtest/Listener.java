package io.jexxa.jexxatest.integrationtest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Listener {
    Listener awaitMessage(int timeout, TimeUnit timeUnit);

    List<String> getAll();

    <T> T pop(Class<T> clazz);
    void clear();
}
