package io.jexxa.adapterapi;

import java.util.ArrayList;
import java.util.List;

public class JexxaContext {
    private static final JexxaContext INSTANCE = new JexxaContext();

    private final List<Runnable> cleanupHandler = new ArrayList<>();
    private final List<Runnable> initHandler = new ArrayList<>();

    public static void registerInitHandler(Runnable cleanupHandler)
    {
        INSTANCE.initHandler.add(cleanupHandler);
    }

    public static void registerCleanupHandler(Runnable cleanupHandler)
    {
        INSTANCE.cleanupHandler.add(cleanupHandler);
    }

    public static void cleanup()
    {
        INSTANCE.cleanupHandler.forEach(Runnable::run);
    }

    public static void init()
    {
        INSTANCE.initHandler.forEach(Runnable::run);
    }

    private JexxaContext()
    {

    }
}
