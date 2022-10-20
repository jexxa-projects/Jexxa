package io.jexxa.jexxatest.architecture.invalidapplication.infrastructure.drivingadapter.rest;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidRepository;

@DrivingAdapter
public class InvalidRESTDrivingAdapter {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final InvalidRepository invalidRepository;

    public InvalidRESTDrivingAdapter(InvalidRepository invalidRepository)
    {
        this.invalidRepository = invalidRepository;
    }
}
