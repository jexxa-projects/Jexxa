package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivingadapter.rest;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.TestApplicationService;

@SuppressWarnings("unused")
@DrivingAdapter
public class RESTDrivingAdapter {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final TestApplicationService testApplicationService;

    public RESTDrivingAdapter(TestApplicationService testApplicationService)
    {
        this.testApplicationService = testApplicationService;
    }
}
