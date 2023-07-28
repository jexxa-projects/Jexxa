package io.jexxa.jexxatest.architecture.invalidapplication.applicationservice;

import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidAggregate;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid.InvalidRepository;

import java.util.List;

@SuppressWarnings("unused")

public class InvalidApplicationService
{
    private final InvalidRepository invalidRepository;
    @SuppressWarnings("FieldCanBeLocal")
    private InvalidAggregate invalidAggregate;

    public InvalidApplicationService(InvalidRepository invalidRepository)
    {
        this.invalidRepository = invalidRepository;
    }

    public List<InvalidAggregate> get()
    {
        return invalidRepository.get();
    }

    public void get(InvalidAggregate invalidAggregate)
    {
        this.invalidAggregate = invalidAggregate;
    }

}
