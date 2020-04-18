package io.ddd.jexxa.core;

import java.util.function.Consumer;

public class BootstrapService<T>
{
    Class<T> bootstrapService;
    JexxaMain jexxaMain;

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
