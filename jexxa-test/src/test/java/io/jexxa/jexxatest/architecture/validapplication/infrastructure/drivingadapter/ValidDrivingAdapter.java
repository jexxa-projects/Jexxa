package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivingadapter;

import io.jexxa.jexxatest.architecture.validapplication.applicationservice.ValidApplicationService;

public class ValidDrivingAdapter {
    private final ValidApplicationService validApplicationService;

    public ValidDrivingAdapter(ValidApplicationService validApplicationService)
    {
        this.validApplicationService = validApplicationService;
    }
}
