package io.jexxa.jexxatest.architecture.invalidapplication.domain.aggregate;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.InvalidApplicationService;

@Aggregate
@SuppressWarnings("all") // As the name states. This is an invalid object used for testing purpose
public class InvalidAggregate {
    private final InvalidApplicationService layerViolation;

    public InvalidAggregate(InvalidApplicationService layerViolation)
    {
        this.layerViolation = layerViolation;
    }
}
