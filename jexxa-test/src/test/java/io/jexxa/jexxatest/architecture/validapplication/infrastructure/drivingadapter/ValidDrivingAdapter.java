package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivingadapter;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.ValidApplicationService;

@DrivingAdapter
public class ValidDrivingAdapter {
    private final ValidApplicationService validApplicationService;

    public ValidDrivingAdapter(ValidApplicationService validApplicationService)
    {
        this.validApplicationService = validApplicationService;
    }
}
