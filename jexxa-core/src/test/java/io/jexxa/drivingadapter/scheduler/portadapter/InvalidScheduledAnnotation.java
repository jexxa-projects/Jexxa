package io.jexxa.drivingadapter.scheduler.portadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.drivingadapter.scheduler.Scheduled;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class InvalidScheduledAnnotation {
    private final SimpleApplicationService simpleApplicationService;

    public InvalidScheduledAnnotation(SimpleApplicationService simpleApplicationService) {
        this.simpleApplicationService = simpleApplicationService;
    }

    @SuppressWarnings("unused")
    @Scheduled(timeUnit = MILLISECONDS)
    public void run() {
        simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue() + 1);
    }
}
