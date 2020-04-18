package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class  DrivingAdapter<T extends IDrivingAdapter>
{
    private final JexxaMain jexxaMain;
    Class<T> drivingAdapter;

    DrivingAdapter(Class<T> drivingAdapter, JexxaMain jexxaMain)
    {
        Validate.notNull(drivingAdapter);
        Validate.notNull(jexxaMain);
        this.drivingAdapter = drivingAdapter;
        this.jexxaMain = jexxaMain;
    }

    public <P> JexxaMain to(Class<P> port)
    {
        if ( isPortWrapper(port)) {
            return jexxaMain.bindToPortWrapper(drivingAdapter, port);
        }   else {
            return jexxaMain.bindToPort(drivingAdapter, port);
        }
    }

    public JexxaMain to(Object port)
    {
        return jexxaMain.bindToPort(drivingAdapter, port);
    }

    public <P extends Annotation> JexxaMain toAnnotation(Class<P> annotation)
    {
        return jexxaMain.bindToAnnotatedPorts(drivingAdapter, annotation);
    }

    private <P> boolean isPortWrapper(Class<P> port)
    {
        return Arrays.stream(port.getConstructors())
                .filter(constructor -> constructor.getParameterTypes().length == 1)
                .anyMatch(constructor -> !constructor.getParameterTypes()[0].isInterface());
    }


}
