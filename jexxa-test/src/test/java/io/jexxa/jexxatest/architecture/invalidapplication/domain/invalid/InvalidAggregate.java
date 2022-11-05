package io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.TestApplicationService;

@SuppressWarnings("unused")
@Aggregate
public class InvalidAggregate {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final TestApplicationService layerViolation;

    public InvalidAggregate(TestApplicationService layerViolation)
    {
        this.layerViolation = layerViolation;
    }
}
