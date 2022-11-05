package io.jexxa.infrastructure.drivingadapter.scheduler.portadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.infrastructure.drivingadapter.scheduler.Scheduled;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FixedRateScheduler {
    private final SimpleApplicationService simpleApplicationService;

    public FixedRateScheduler(SimpleApplicationService simpleApplicationService) {
        this.simpleApplicationService = simpleApplicationService;
    }

    @Scheduled(fixedRate = 10, timeUnit = MILLISECONDS)
    @SuppressWarnings("unused")
    public void run() {
        simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue() + 1);
    }
}
