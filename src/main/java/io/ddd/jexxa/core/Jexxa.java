package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.Properties;

import io.ddd.jexxa.core.factory.DrivenAdapterFactory;
import io.ddd.jexxa.core.factory.DrivingAdapterFactory;
import io.ddd.jexxa.core.factory.PortFactory;
import io.ddd.jexxa.infrastructure.drivingadapter.CompositeDrivingAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class Jexxa
{
    CompositeDrivingAdapter compositeDrivingAdapter;
    Properties properties;

    DrivingAdapterFactory drivingAdapterFactory;
    DrivenAdapterFactory drivenAdapterFactory;
    PortFactory portFactory;


    public Jexxa(Properties properties)
    {
        Validate.notNull(properties);
        compositeDrivingAdapter = new CompositeDrivingAdapter();
        this.properties = properties;

        drivingAdapterFactory = new DrivingAdapterFactory();
        drivenAdapterFactory = new DrivenAdapterFactory();
        portFactory = new PortFactory(drivenAdapterFactory);
    }

    public Jexxa whiteListDrivingAdapterPackage(String packageName)
    {
        //drivingAdapterFactory.whiteListPackage(packageName);
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
        //drivingAdapterFactory.whiteListPackage(packageName);
        drivenAdapterFactory.whiteListPackage(packageName);
        portFactory.whiteListPackage(packageName);
        return this;
    }


    public void bind(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.newInstanceOf(adapter, properties);
        var inboundPort    = portFactory.newInstanceOf(port, properties);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

   
    public void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> portAnnotation) {
        Validate.notNull(adapter);
        Validate.notNull(portAnnotation);

        //Create ports and adapter
        var drivingAdapter = drivingAdapterFactory.newInstanceOf(adapter, properties);

        var portList = portFactory.createPortsBy(portAnnotation, properties);
        portList.forEach(drivingAdapter::register);
        
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
