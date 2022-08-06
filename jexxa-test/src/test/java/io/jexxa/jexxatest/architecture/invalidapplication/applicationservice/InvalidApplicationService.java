package io.jexxa.jexxatest.architecture.invalidapplication.applicationservice;

import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidAggregate;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidRepository;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidValueObject;
@SuppressWarnings("unused")

public class InvalidApplicationService
{
    private final InvalidRepository invalidRepository;

    public InvalidApplicationService(InvalidRepository invalidRepository)
    {
        this.invalidRepository = invalidRepository;
    }

    public InvalidAggregate get(InvalidValueObject invalidValueObject)
    {
        return invalidRepository.get(invalidValueObject);
    }

}
