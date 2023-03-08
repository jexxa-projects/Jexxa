package io.jexxa.jexxatest.architecture.validapplication.applicationservice;

import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidEnum;

@SuppressWarnings("unused")
@ApplicationService
public class TestApplicationService
{
    //Using enum in switch statement creates anonymous class
    public ValidEnum useEnum(ValidEnum validEnum)
    {
        return switch (validEnum) {
            case VALUE_B -> ValidEnum.VALUE_B;
            case VALUE_C -> ValidEnum.VALUE_C;
            default -> ValidEnum.VALUE_A;
        };
    }
}
