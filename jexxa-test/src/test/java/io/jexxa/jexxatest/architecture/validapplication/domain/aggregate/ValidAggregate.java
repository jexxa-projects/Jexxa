package io.jexxa.jexxatest.architecture.validapplication.domain.aggregate;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;

@Aggregate
public class ValidAggregate {
    private ValidValueObject validValueObjectA;

    public ValidAggregate(ValidValueObject validValueObject)
    {
        this.validValueObjectA = validValueObject;
    }

    public ValidValueObject getValidValueObjectA()
    {
        return validValueObjectA;
    }

    public void setValidValueObjectA(ValidValueObject validValueObjectA)
    {
        this.validValueObjectA = validValueObjectA;
    }

}
