package io.jexxa.jexxatest.architecture.validapplication.domain.aggregate;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateID;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;
import io.jexxa.jexxatest.architecture.validapplication.domainservice.IDomainEventSender;

@Aggregate
@SuppressWarnings("unused")
public class AggregateWithInfrastructureService
{
    private final ValidValueObject validValueObjectA;

    public AggregateWithInfrastructureService(ValidValueObject validValueObject, IDomainEventSender domainEventSender)
    {
        this.validValueObjectA = validValueObject;
    }

    @AggregateID
    public ValidValueObject getValidValueObjectA()
    {
        return validValueObjectA;
    }


}
