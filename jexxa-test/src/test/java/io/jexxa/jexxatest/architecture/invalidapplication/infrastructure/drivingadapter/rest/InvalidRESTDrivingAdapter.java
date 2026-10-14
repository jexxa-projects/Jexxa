package io.jexxa.jexxatest.architecture.invalidapplication.infrastructure.drivingadapter.rest;

import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidRepository;

@SuppressWarnings("unused")
//@DrivingAdapter missing makes it invalid
public class InvalidRESTDrivingAdapter {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final InvalidRepository invalidRepository;

    public InvalidRESTDrivingAdapter(InvalidRepository invalidRepository)
    {
        this.invalidRepository = invalidRepository;
    }
}
