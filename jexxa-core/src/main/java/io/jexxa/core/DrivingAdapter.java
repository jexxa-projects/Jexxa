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
    private final BooleanSupplier booleanSupplier;

    DrivingAdapter(BooleanSupplier booleanSupplier, Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        Validate.notNull(booleanSupplier);
        Validate.notNull(drivingAdapterClass);
        Validate.notNull(jexxaMain);

        AdapterConvention.validate(drivingAdapterClass);

        this.drivingAdapterClass = drivingAdapterClass;
        this.jexxaMain = jexxaMain;
        this.booleanSupplier = booleanSupplier;
    }

    DrivingAdapter(Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        this(() -> true, drivingAdapterClass, jexxaMain);
    }

    public <P> JexxaMain to(Class<P> port)
    {
        Validate.notNull(port);

        if ( !booleanSupplier.getAsBoolean())
        {
            return jexxaMain;
        }

        if ( AdapterConvention.isPortAdapter(port))
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

        if ( !booleanSupplier.getAsBoolean())
        {
            return jexxaMain;
        }

        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        Validate.notNull(annotation);

        if ( !booleanSupplier.getAsBoolean())
        {
            return jexxaMain;
        }

        jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
        return jexxaMain;
    }

}
