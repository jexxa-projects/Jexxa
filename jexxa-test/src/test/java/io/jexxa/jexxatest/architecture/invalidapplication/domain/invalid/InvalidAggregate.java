package io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.TestApplicationService;

@Aggregate
@SuppressWarnings("all") // As the name states. This is an invalid object used for testing purpose
public class InvalidAggregate {
    private final TestApplicationService layerViolation;

    public InvalidAggregate(TestApplicationService layerViolation)
    {
        this.layerViolation = layerViolation;
    }
}
