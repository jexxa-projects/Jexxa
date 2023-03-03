package io.jexxa.core;

import io.jexxa.common.annotation.CheckReturnValue;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BootstrapService<T>
{
    private final Class<T> bootstrapServiceClass;
    private final JexxaMain jexxaMain;

    BootstrapService(Class<T> bootstrapService, JexxaMain jexxaMain)
    {
        this.bootstrapServiceClass = Objects.requireNonNull(bootstrapService);
        this.jexxaMain = Objects.requireNonNull(jexxaMain);
    }

    @CheckReturnValue
    public JexxaMain with(Consumer<T> initFunction)
    {
        jexxaMain.addBootstrapService(bootstrapServiceClass, initFunction);
        return jexxaMain;
    }

    @CheckReturnValue
    public JexxaMain and()
    {
        jexxaMain.addBootstrapService(bootstrapServiceClass, noInitFunction());
        return jexxaMain;
    }

    public static <T> Consumer<T> noInitFunction()
    {
        return element -> { /* just an empty function */};
    }
}
