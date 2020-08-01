package io.jexxa.core;

import java.lang.annotation.Annotation;

import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.CheckReturnValue;
import org.apache.commons.lang3.Validate;

public class  DrivingAdapter<T extends IDrivingAdapter>
{
    private final JexxaMain jexxaMain;
    private final Class<T> drivingAdapterClass;

    DrivingAdapter(Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        Validate.notNull(drivingAdapterClass);
        Validate.notNull(jexxaMain);

        AdapterConvention.validate(drivingAdapterClass);

        this.drivingAdapterClass = drivingAdapterClass;
        this.jexxaMain = jexxaMain;
    }

    @CheckReturnValue
    public <P> JexxaMain to(Class<P> port)
    {
        Validate.notNull(port);

        if ( AdapterConvention.isPortAdapter(port)) {
            jexxaMain.bindToPortAdapter(drivingAdapterClass, port);
            return jexxaMain;
        }

        PortConvention.validate(port);
        jexxaMain.bindToPort(drivingAdapterClass, port);
        return jexxaMain;
    }

    @CheckReturnValue
    public JexxaMain to(Object port)
    {
        Validate.notNull(port);
        
        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    @CheckReturnValue
    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        Validate.notNull(annotation);

        jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
        return jexxaMain;
    }

}
