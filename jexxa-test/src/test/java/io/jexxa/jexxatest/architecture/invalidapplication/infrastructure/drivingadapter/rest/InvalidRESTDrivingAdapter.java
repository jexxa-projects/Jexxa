package io.jexxa.jexxatest.architecture.invalidapplication.infrastructure.drivingadapter.rest;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidRepository;

@DrivingAdapter
@SuppressWarnings("all") // As the name states. This is an invalid object used for testing purpose
public class InvalidRESTDrivingAdapter {
    private final InvalidRepository invalidRepository;

    public InvalidRESTDrivingAdapter(InvalidRepository invalidRepository)
    {
        this.invalidRepository = invalidRepository;
    }
}
