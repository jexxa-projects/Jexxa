package io.jexxa.drivingadapter.scheduler.portadapter;

import io.jexxa.drivingadapter.scheduler.Scheduled;
import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.testapplication.domain.model.JexxaValueObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MultipleIncrementer {
    private final SimpleApplicationService simpleApplicationService;

    public MultipleIncrementer(SimpleApplicationService simpleApplicationService) {
        this.simpleApplicationService = simpleApplicationService;
    }

    @Scheduled(fixedRate = 10, timeUnit = MILLISECONDS)
    @SuppressWarnings("unused")
    public void incrementCounter() {
        simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue() + 1);
    }

    @Scheduled(fixedDelay = 10, timeUnit = MILLISECONDS)
    @SuppressWarnings("unused")
    public void incrementValueObject() {
        simpleApplicationService.setSimpleValueObject(new JexxaValueObject(simpleApplicationService.getSimpleValueObject().getValue()+1));
    }

}
