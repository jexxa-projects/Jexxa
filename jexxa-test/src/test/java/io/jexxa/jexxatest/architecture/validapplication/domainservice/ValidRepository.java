package io.jexxa.jexxatest.architecture.validapplication.domainservice;


import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.jexxatest.architecture.validapplication.ValidApplication;
import io.jexxa.jexxatest.architecture.validapplication.domain.aggregate.ValidAggregate;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;

@Repository
public interface ValidRepository
{
    void add(ValidAggregate validAggregate);
    ValidAggregate get(ValidValueObject validValueObject);
}
