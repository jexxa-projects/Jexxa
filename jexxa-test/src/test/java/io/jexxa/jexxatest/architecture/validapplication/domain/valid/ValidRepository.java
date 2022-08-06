package io.jexxa.jexxatest.architecture.validapplication.domain.valid;


import io.jexxa.addend.applicationcore.Repository;

@Repository
@SuppressWarnings("unused")
public interface ValidRepository
{
    void add(ValidAggregate validAggregate);
    ValidAggregate get(ValidValueObject validValueObject);
}
