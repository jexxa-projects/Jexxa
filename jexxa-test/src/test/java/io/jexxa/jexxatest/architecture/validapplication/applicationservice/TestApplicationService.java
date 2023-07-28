package io.jexxa.jexxatest.architecture.validapplication.applicationservice;

import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidAggregate;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidEnum;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidRepository;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidValueObject;

import java.util.List;

@SuppressWarnings("unused")
@ApplicationService
public class TestApplicationService
{
    private final ValidRepository validRepository;
    public TestApplicationService(ValidRepository validRepository)
    {
        this.validRepository = validRepository;
    }
    //Using enum in switch statement creates anonymous class
    public ValidEnum useEnum(ValidEnum validEnum)
    {
        return switch (validEnum) {
            case VALUE_B -> ValidEnum.VALUE_B;
            case VALUE_C -> ValidEnum.VALUE_C;
            default -> ValidEnum.VALUE_A;
        };
    }

    public List<ValidValueObject> get() {
        return validRepository.get().stream()
                .map(ValidAggregate::getValidValueObjectA)
                .toList();
    }
}
