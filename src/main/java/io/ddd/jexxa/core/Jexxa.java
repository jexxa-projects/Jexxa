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

    AdapterFactory drivingAdapterFactory;
    AdapterFactory drivenAdapterFactory;
    PortFactory portFactory;


    public Jexxa(Properties properties)
    {
        Validate.notNull(properties);
        compositeDrivingAdapter = new CompositeDrivingAdapter();
        this.properties = properties;

        drivingAdapterFactory = new AdapterFactory();
        drivenAdapterFactory = new AdapterFactory();
        portFactory = new PortFactory(drivenAdapterFactory);
    }

    public Jexxa whiteListDrivingAdapterPackage(String packageName)
    {
        drivingAdapterFactory.whiteListPackage(packageName);
        return this;
    }

    public Jexxa whiteListDrivenAdapterPackage(String packageName)
    {
        drivenAdapterFactory.whiteListPackage(packageName);
        return this;
    }

    public Jexxa whiteListPortPackage(String packageName)
    {
        portFactory.whiteListPackage(packageName);
        return this;
    }

    public Jexxa whiteListPackage(String packageName)
    {
        drivingAdapterFactory.whiteListPackage(packageName);
        drivenAdapterFactory.whiteListPackage(packageName);
        portFactory.whiteListPackage(packageName);
        return this;
    }


    public void bind(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.createByType(adapter, properties);
        var inboundPort    = ClassFactory.createByConstructor(port);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

   
    public void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var annotationScanner = new DependencyScanner();
        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(port);

        //Create ports and adapter
        var drivingAdapter = drivingAdapterFactory.createByType(adapter, properties);
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
