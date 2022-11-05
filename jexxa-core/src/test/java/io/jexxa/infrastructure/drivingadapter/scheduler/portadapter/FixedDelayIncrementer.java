package io.jexxa.infrastructure.drivingadapter.scheduler.portadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.infrastructure.drivingadapter.scheduler.Scheduled;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FixedDelayIncrementer {
    private final SimpleApplicationService simpleApplicationService;

    public FixedDelayIncrementer(SimpleApplicationService simpleApplicationService) {
        this.simpleApplicationService = simpleApplicationService;
    }

    @Scheduled(fixedDelay = 10, timeUnit = MILLISECONDS)
    @SuppressWarnings("unused")
    public void incrementCounter() {
        simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue() + 1);
    }
}
