package io.ddd.jexxa.application.domain.aggregate;

import io.ddd.jexxa.application.annotation.Aggregate;
import io.ddd.jexxa.application.annotation.AggregateID;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;

@Aggregate
public class JexxaAggregate
{
    private final JexxaValueObject jexxaValueObject;

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
