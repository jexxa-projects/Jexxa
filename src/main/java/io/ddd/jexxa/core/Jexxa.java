package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Properties;

import io.ddd.jexxa.infrastructure.drivingadapter.CompositeDrivingAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class Jexxa
{
    CompositeDrivingAdapter compositeDrivingAdapter;
    Properties properties;

    public Jexxa(Properties properties)
    {
        Validate.notNull(properties);
        compositeDrivingAdapter = new CompositeDrivingAdapter();
        this.properties = properties;
    }

    public void bind(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = ClassFactory.createByConstructor(adapter, properties);
        Validate.notNull(drivingAdapter);

        var inboundPort = ClassFactory.createByConstructor(port);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    public void bindByAnnotation(Class<? extends Annotation> adapter, Class<? extends Annotation> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var annotationScanner = new DependencyScanner();
        var scannedDrivingAdapters = annotationScanner.getClassesWithAnnotation(adapter);
        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(port);

        //Create ports
        var createdDrivingAdapters = new ArrayList<IDrivingAdapter>();

        scannedDrivingAdapters.forEach(element -> createdDrivingAdapters.add((IDrivingAdapter)ClassFactory.createByConstructor(element, properties)));
        Validate.isTrue(scannedDrivingAdapters.size() == createdDrivingAdapters.size());

        var createdInboundPorts = new ArrayList<>();
        scannedInboundPorts.forEach(element -> createdInboundPorts.add(ClassFactory.createByConstructor(element)));
        Validate.isTrue(scannedInboundPorts.size() == createdInboundPorts.size());

        //register ports and adapter
        createdDrivingAdapters.forEach(drivingAdapter -> createdInboundPorts.forEach(drivingAdapter::register));
        createdDrivingAdapters.forEach(drivingAdapter -> compositeDrivingAdapter.add(drivingAdapter));
    }


    public void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var annotationScanner = new DependencyScanner();
        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(port);

        //Create ports and adapter
        var drivingAdapter = ClassFactory.createByConstructor(adapter, properties);
        Validate.notNull(drivingAdapter);

        scannedInboundPorts.forEach(element -> drivingAdapter.register(ClassFactory.createByConstructor(element)));

        //register ports and adapter
        compositeDrivingAdapter.add(drivingAdapter);
    }


    void startDrivingAdapters()
    {
        compositeDrivingAdapter.start();
    }

    void stopDrivingAdapters()
    {
        compositeDrivingAdapter.stop();
    }
}
