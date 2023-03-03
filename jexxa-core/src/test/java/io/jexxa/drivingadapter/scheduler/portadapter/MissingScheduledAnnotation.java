package io.jexxa.drivingadapter.scheduler.portadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;

public class MissingScheduledAnnotation {
    private final SimpleApplicationService simpleApplicationService;

    public MissingScheduledAnnotation(SimpleApplicationService simpleApplicationService) {
        this.simpleApplicationService = simpleApplicationService;
    }

    @SuppressWarnings("unused")
    public void run() {
        simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue() + 1);
    }
}
