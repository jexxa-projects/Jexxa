package io.jexxa.infrastructure.drivingadapter.scheduler;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.JexxaLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler implements IDrivingAdapter
{
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    private final Map<Object, List<Method>> scheduledMethods = new HashMap<>();

    @Override
    public void register(Object port) {
        validateSchedulerConfiguration(port);
        scheduledMethods.put(port, getSchedulerConfiguration(port));
    }

    @Override
    public void start() {
        scheduledMethods.forEach(this::registerObject);
    }

    private void registerObject(Object port, List<Method> scheduledMethods)
    {
        scheduledMethods.forEach( method ->
        {
            var schedulerConfiguration = method.getAnnotation(Scheduled.class);
            executorService.scheduleAtFixedRate(() -> {
                try {
                    method.invoke(port);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    JexxaLogger.getLogger(Scheduler.class).warn("Could not execute method {}::{}.",port.getClass().getSimpleName(), method.getName()) ;
                }
            }, schedulerConfiguration.initialDelay(), schedulerConfiguration.fixedRate(), schedulerConfiguration.timeUnit());
        });
    }

    @Override
    public void stop() {
        executorService.shutdown();
        try
        {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS))
            {
                executorService.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            executorService.shutdownNow();
            JexxaLogger.getLogger(Scheduler.class).warn("ExecutorService could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }

    void validateSchedulerConfiguration(Object object)
    {
        if (getSchedulerConfiguration(object).isEmpty())
        {
            throw new IllegalArgumentException(
                    String.format("Given object %s does not provide a %s for any public method!"
                            , object.getClass().getSimpleName()
                            , Scheduled.class.getSimpleName()));
        }
    }

    private List<Method> getSchedulerConfiguration(Object object)
    {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Scheduled.class))
                .filter(method -> method.getParameterCount() == 0)
                .toList();
    }
}
