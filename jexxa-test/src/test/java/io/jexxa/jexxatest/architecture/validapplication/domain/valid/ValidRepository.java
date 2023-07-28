package io.jexxa.jexxatest.architecture.validapplication.domain.valid;


import io.jexxa.addend.applicationcore.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface ValidRepository
{
    void add(ValidAggregate validAggregate);
    ValidAggregate get(ValidValueObject validValueObject);

    List<ValidAggregate> get();
}
