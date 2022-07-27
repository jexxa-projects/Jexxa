package io.jexxa.jexxatest.architecture.invalidapplication.infrastructure.drivingadapter;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.InvalidApplicationService;

@DrivingAdapter
@SuppressWarnings("all") // As the name states. This is an invalid object used for testing purpose
public class InvalidDrivingAdapter {
    private final InvalidApplicationService invalidApplicationService;

    public InvalidDrivingAdapter(InvalidApplicationService invalidApplicationService)
    {
        this.invalidApplicationService = invalidApplicationService;
    }
}
