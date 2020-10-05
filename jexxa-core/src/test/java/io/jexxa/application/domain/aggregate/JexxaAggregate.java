package io.jexxa.application.domain.aggregate;

import io.jexxa.application.annotation.Aggregate;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

@Aggregate
public class JexxaAggregate
{
    private final JexxaEntity jexxaEntity;
    private final JexxaValueObject jexxaValueObject;

    private JexxaAggregate(JexxaValueObject jexxaValueObject)
    {
        this.jexxaEntity = JexxaEntity.create(jexxaValueObject);
        this.jexxaValueObject = jexxaValueObject;
    }

    public JexxaValueObject getKey()
    {
        return jexxaValueObject;
    }

    public static JexxaAggregate create(JexxaValueObject key)
    {
        return new JexxaAggregate(key);
    }

}
