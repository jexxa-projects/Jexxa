package io.jexxa.jexxatest.architecture.validapplication.domainservice;

import io.jexxa.addend.applicationcore.DomainService;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidEnum;

@SuppressWarnings("unused")
@DomainService
public class TestDomainService
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
