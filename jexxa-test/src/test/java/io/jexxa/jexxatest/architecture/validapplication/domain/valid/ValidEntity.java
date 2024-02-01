package io.jexxa.jexxatest.architecture.validapplication.domain.valid;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateID;

@Aggregate
@SuppressWarnings("unused")
public class ValidEntity {
    private final ValidValueObject aggregateKey;

    public ValidEntity(ValidValueObject aggregateKey)
    {
        this.aggregateKey = aggregateKey;
    }

    @AggregateID
    public ValidValueObject getAggregateKey()
    {
        return aggregateKey;
    }
}
