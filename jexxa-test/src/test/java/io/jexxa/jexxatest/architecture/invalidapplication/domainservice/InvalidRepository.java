package io.jexxa.jexxatest.architecture.invalidapplication.domainservice;


import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.aggregate.InvalidAggregate;
import io.jexxa.jexxatest.architecture.invalidapplication.domain.valueobject.InvalidValueObject;

@Repository
public interface InvalidRepository
{
    void add(InvalidAggregate invalidAggregate);
    InvalidAggregate get(InvalidValueObject invalidValueObject);
}
