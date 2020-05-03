package io.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.Arrays;

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
        this.drivingAdapterClass = drivingAdapterClass;
        this.jexxaMain = jexxaMain;
    }

    public <P> JexxaMain to(Class<P> port)
    {
        if ( isPortWrapper(port)) {
            return jexxaMain.bindToPortAdapter(drivingAdapterClass, port);
        }   else {
            return jexxaMain.bindToPort(drivingAdapterClass, port);
        }
    }

    public JexxaMain to(Object port)
    {
        return jexxaMain.bindToPort(drivingAdapterClass, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        return jexxaMain.bindToAnnotatedPorts(drivingAdapterClass, annotation);
    }

    private <P> boolean isPortWrapper(Class<P> port)
    {
        return Arrays.stream(port.getConstructors())
                .filter(constructor -> constructor.getParameterTypes().length == 1)
                .anyMatch(constructor -> !constructor.getParameterTypes()[0].isInterface());
    }


}
