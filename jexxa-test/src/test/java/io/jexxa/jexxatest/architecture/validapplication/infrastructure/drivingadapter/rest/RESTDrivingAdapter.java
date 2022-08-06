package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivingadapter.rest;

import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.TestApplicationService;

@DrivingAdapter
@SuppressWarnings("all") //Class used for testing purpose
public class RESTDrivingAdapter {
    private final TestApplicationService testApplicationService;

    public RESTDrivingAdapter(TestApplicationService testApplicationService)
    {
        this.testApplicationService = testApplicationService;
    }
}
