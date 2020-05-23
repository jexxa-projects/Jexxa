package io.jexxa.core;

import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class BootstrapService<T>
{
    final Class<T> bootstrapServiceClass;
    final JexxaMain jexxaMain;

    BootstrapService(Class<T> bootstrapService, JexxaMain jexxaMain)
    {
        this.bootstrapServiceClass = bootstrapService;
        this.jexxaMain = jexxaMain;
    }

    public JexxaMain with(Consumer<T> initFunction)
    {
        jexxaMain.addBootstrapService(bootstrapServiceClass, initFunction);
        return jexxaMain;
    }
}
