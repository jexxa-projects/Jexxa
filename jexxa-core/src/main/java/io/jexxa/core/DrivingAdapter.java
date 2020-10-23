package io.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.function.BooleanSupplier;

import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang3.Validate;

public class  DrivingAdapter<T extends IDrivingAdapter>
{
    private final JexxaMain jexxaMain;
    private final Class<T> drivingAdapterClass;
    private final BooleanSupplier conditionalBind;

    DrivingAdapter(Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        this(() -> true, drivingAdapterClass, jexxaMain);
    }

    DrivingAdapter(BooleanSupplier condictionalBind, Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        Validate.notNull(condictionalBind);
        Validate.notNull(drivingAdapterClass);
        Validate.notNull(jexxaMain);

        AdapterConvention.validate(drivingAdapterClass);

        this.drivingAdapterClass = drivingAdapterClass;
        this.jexxaMain = jexxaMain;
        this.conditionalBind = condictionalBind;
    }

    public <P> JexxaMain to(Class<P> port)
    {
        Validate.notNull(port);

        if ( !conditionalBind.getAsBoolean())
        {
            return jexxaMain;
        }

        if ( AdapterConvention.isPortAdapter(port, jexxaMain.getInfrastructure()))
        {
            jexxaMain.bindToPortAdapter(drivingAdapterClass, port);
            return jexxaMain;
        }

        PortConvention.validate(port);

        jexxaMain.bindToPort(drivingAdapterClass, port);

        return jexxaMain;
    }

    public JexxaMain to(Object port)
    {
        Validate.notNull(port);

        if ( !conditionalBind.getAsBoolean())
        {
            return jexxaMain;
        }

        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        Validate.notNull(annotation);

        if ( !conditionalBind.getAsBoolean())
        {
            return jexxaMain;
        }

        jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
        return jexxaMain;
    }

}
