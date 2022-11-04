package io.jexxa.infrastructure.drivingadapter.scheduler;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.utils.JexxaLogger;

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
        var invocationHandler = InvocationManager.getInvocationHandler(port);
        scheduledMethods.forEach( method ->
        {
            var schedulerConfiguration = method.getAnnotation(Scheduled.class);
            if (schedulerConfiguration.fixedRate() >= 0) {
                executorService.scheduleAtFixedRate(
                        () -> invocationHandler.invoke(method, port, new Object[0]),
                        schedulerConfiguration.initialDelay(),
                        schedulerConfiguration.fixedRate(),
                        schedulerConfiguration.timeUnit());
            } else {
                executorService.scheduleWithFixedDelay(
                        () -> invocationHandler.invoke(method, port, new Object[0]),
                        schedulerConfiguration.initialDelay(),
                        schedulerConfiguration.fixedDelay(),
                        schedulerConfiguration.timeUnit());
            }
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

    private void validateSchedulerConfiguration(Object object)
    {
        var scheduledConfiguration = getSchedulerConfiguration(object);
        if (scheduledConfiguration.isEmpty())
        {
            throw new IllegalArgumentException(
                    String.format("Given object %s does not provide a %s annotation for any public method!"
                            , object.getClass().getSimpleName()
                            , Scheduled.class.getSimpleName()));
        }

        var result = scheduledConfiguration
                .stream()
                .filter( element ->
                        (element.getAnnotation(Scheduled.class).fixedDelay() < 0 && element.getAnnotation(Scheduled.class).fixedRate() < 0)
                     || (element.getAnnotation(Scheduled.class).fixedDelay() >= 0 && element.getAnnotation(Scheduled.class).fixedRate() >= 0))
                .findAny();

        if (result.isPresent()) {
                throw new IllegalArgumentException(
                    String.format("Given method %s::%s does not provide a valid  value for `fixedInterval` or `fixedDelay` in @Scheduled (exact one of these values must be >=0)!"
                            , object.getClass().getSimpleName()
                            , result.get().getName()));
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
