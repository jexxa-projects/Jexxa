package io.jexxa.jexxatest.architecture.invalidapplication.domain.aggregate;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.jexxatest.architecture.validapplication.applicationservice.InvalidApplicationService;

@Aggregate
public class InvalidAggregate {
    private final InvalidApplicationService layerViolation;

    public InvalidAggregate(InvalidApplicationService layerViolation)
    {
        this.layerViolation = layerViolation;
    }
}
