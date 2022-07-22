package io.jexxa.jexxatest.architecture.invalidapplication.infrastructure.drivingadapter;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.InvalidApplicationService;

@DrivingAdapter
public class InvalidDrivingAdapter {
    private final InvalidApplicationService invalidApplicationService;

    public InvalidDrivingAdapter(InvalidApplicationService invalidApplicationService)
    {
        this.invalidApplicationService = invalidApplicationService;
    }
}
