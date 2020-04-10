package io.ddd.jexxa.dummyapplication.domain.aggregate;

import io.ddd.jexxa.dummyapplication.annotation.Aggregate;
import io.ddd.jexxa.dummyapplication.annotation.AggregateID;
import io.ddd.jexxa.dummyapplication.domain.valueobject.JexxaValueObject;

@Aggregate
public class JexxaAggregate
{
    private JexxaValueObject jexxaValueObject;

    public static JexxaAggregate create(JexxaValueObject key)
    {
        return new JexxaAggregate(key);
    }


    @AggregateID
    public JexxaValueObject getKey()
    {
        return jexxaValueObject;
    }

    private JexxaAggregate(JexxaValueObject jexxaValueObject)
    {
        this.jexxaValueObject = jexxaValueObject;
    }
}
