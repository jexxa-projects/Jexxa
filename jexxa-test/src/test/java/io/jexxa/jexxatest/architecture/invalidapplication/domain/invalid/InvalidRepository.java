package io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid;


import io.jexxa.addend.applicationcore.Repository;

@Repository
public interface InvalidRepository
{
    void add(InvalidAggregate invalidAggregate);
    InvalidAggregate get(InvalidValueObject invalidValueObject);
}
