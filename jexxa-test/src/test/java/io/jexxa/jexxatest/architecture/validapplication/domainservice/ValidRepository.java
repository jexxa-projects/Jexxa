package io.jexxa.jexxatest.architecture.validapplication.domainservice;


import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.jexxatest.architecture.validapplication.domain.aggregate.ValidAggregate;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;

@Repository
@SuppressWarnings("unused")
public interface ValidRepository
{
    void add(ValidAggregate validAggregate);
    ValidAggregate get(ValidValueObject validValueObject);
}
