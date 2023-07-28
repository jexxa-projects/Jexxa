package io.jexxa.jexxatest.architecture.validapplication.domain.valid;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateID;

@Aggregate
@SuppressWarnings("unused")
public class ValidAggregate {
    private final ValidValueObject validValueObjectA;

    public ValidAggregate(ValidValueObject validValueObject)
    {
        this.validValueObjectA = validValueObject;
    }

    @AggregateID
    public ValidValueObject getValidValueObjectA()
    {
        return validValueObjectA;
    }

    public void domainLogic()
    {
        // Empty implementation because method is used to validate architecture rules
    }

}
