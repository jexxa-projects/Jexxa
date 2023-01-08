package io.jexxa.jexxatest.integrationtest.messaging;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface MessageListener {
    MessageListener waitUntilMessageReceived(int timeout, TimeUnit timeUnit);

    List<String> getMessages();

    <T> T pop(Class<T> clazz);
    void clear();
}
