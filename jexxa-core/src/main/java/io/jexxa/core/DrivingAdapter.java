package io.jexxa.core;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class  DrivingAdapter<T extends IDrivingAdapter>
{
    private final JexxaMain jexxaMain;
    private final Class<T> drivingAdapterClass;
    private final BooleanSupplier conditionalBind;

    DrivingAdapter(Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        this(() -> true, drivingAdapterClass, jexxaMain);
    }

    DrivingAdapter(BooleanSupplier conditionalBind, Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        AdapterConvention.validate(drivingAdapterClass);

        this.drivingAdapterClass = Objects.requireNonNull(drivingAdapterClass);
        this.jexxaMain = Objects.requireNonNull(jexxaMain);
        this.conditionalBind = Objects.requireNonNull(conditionalBind);
    }

    public <P> JexxaMain to(Class<P> port)
    {
        Objects.requireNonNull(port);

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
        Objects.requireNonNull(port);

        if ( !conditionalBind.getAsBoolean())
        {
            return jexxaMain;
        }

        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        Objects.requireNonNull(annotation);

        if ( !conditionalBind.getAsBoolean())
        {
            return jexxaMain;
        }

        jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
        return jexxaMain;
    }

}
