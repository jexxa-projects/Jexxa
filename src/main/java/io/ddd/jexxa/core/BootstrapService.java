package io.ddd.jexxa.core;

import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class BootstrapService<T>
{
    final Class<T> bootstrapService;
    final JexxaMain jexxaMain;

    BootstrapService(Class<T> bootstrapService, JexxaMain jexxaMain)
    {
        this.bootstrapService = bootstrapService;
        this.jexxaMain = jexxaMain;
    }

    public JexxaMain with(Consumer<T> initFunction)
    {
        jexxaMain.addBootstrapService(bootstrapService, initFunction);
        return jexxaMain;
    }
}
