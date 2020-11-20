package io.jexxa.application.domain.domainevent;

import java.util.Objects;

import io.jexxa.application.annotation.DomainEvent;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

@DomainEvent
public class JexxaDomainEvent
{
    private final JexxaValueObject jexxaValueObject;

    private JexxaDomainEvent( JexxaValueObject jexxaValueObject )
    {
        this.jexxaValueObject = jexxaValueObject;
    }

    public JexxaValueObject getValue()
    {
        return jexxaValueObject;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        JexxaDomainEvent that = (JexxaDomainEvent) o;
        return Objects.equals(jexxaValueObject, that.jexxaValueObject);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jexxaValueObject);
    }

    public static JexxaDomainEvent create(JexxaValueObject jexxaValueObject )
    {
        return new JexxaDomainEvent( jexxaValueObject );
    }
}
