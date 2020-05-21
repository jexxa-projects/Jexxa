package io.jexxa.core;

import java.lang.annotation.Annotation;

import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class  DrivingAdapter<T extends IDrivingAdapter>
{
    private final JexxaMain jexxaMain;
    final Class<T> drivingAdapterClass;

    DrivingAdapter(Class<T> drivingAdapterClass, JexxaMain jexxaMain)
    {
        Validate.notNull(drivingAdapterClass);
        Validate.notNull(jexxaMain);

        AdapterConvention.validate(drivingAdapterClass);

        this.drivingAdapterClass = drivingAdapterClass;
        this.jexxaMain = jexxaMain;
    }

    public <P> JexxaMain to(Class<P> port)
    {
        Validate.notNull(port);

        if ( AdapterConvention.isPortAdapter(port)) {
            return jexxaMain.bindToPortAdapter(drivingAdapterClass, port);
        }

        PortConvention.validate(port);
        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public JexxaMain to(Object port)
    {
        Validate.notNull(port);
        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        return jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
    }

}
