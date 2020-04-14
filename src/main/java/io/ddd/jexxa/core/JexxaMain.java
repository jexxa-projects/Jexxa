package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.Properties;

import io.ddd.jexxa.core.factory.AdapterFactory;
import io.ddd.jexxa.core.factory.PortFactory;
import io.ddd.jexxa.infrastructure.drivingadapter.CompositeDrivingAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

public class JexxaMain
{
    private final CompositeDrivingAdapter compositeDrivingAdapter;
    private final Properties properties = new Properties();

    private final AdapterFactory drivingAdapterFactory;
    private final AdapterFactory drivenAdapterFactory;
    private final PortFactory portFactory;

    private final BoundedContext boundedContext;

    public JexxaMain(String contextName)
    {
        this(contextName, System.getProperties());
    }

    public JexxaMain(String contextName, Properties properties)
    {
        Validate.notNull(properties);
        Validate.notNull(contextName);

        this.boundedContext = new BoundedContext(contextName);
        this.properties.putAll( properties );
        this.properties.put("io.ddd.jexxa.context.name", contextName);

        this.compositeDrivingAdapter = new CompositeDrivingAdapter();

        this.drivingAdapterFactory = new AdapterFactory();
        this.drivenAdapterFactory = new AdapterFactory();
        this.portFactory = new PortFactory(drivenAdapterFactory);
    }

    public JexxaMain whiteListAdapter(String packageName)
    {
        drivenAdapterFactory.whiteListPackage(packageName);
        return this;
    }

    public JexxaMain whiteListPorts(String packageName)
    {
        portFactory.whiteListPackage(packageName);
        return this;
    }

    public JexxaMain whiteList(String packageName)
    {
        whiteListAdapter(packageName);
        whiteListPorts(packageName);
        return this;
    }

    /**
     * @deprecated Use addBootstrapService
     */
    @Deprecated
    public void bindConfigAdapter(Class<? extends IDrivingAdapter> adapter) {
        Validate.notNull(adapter);

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    public void bindToPort(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        var inboundPort    = portFactory.getInstanceOf(port, properties);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    public void bindToPort(Class<? extends IDrivingAdapter> adapter, Object port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        drivingAdapter.register(port);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    public void bindToPortWrapper(Class<? extends IDrivingAdapter> adapter, Class<?> portWrapper)
    {
        var drivingAdapter = drivingAdapterFactory.newInstanceOf(adapter, properties);

        var portWrapperInstance = portFactory.getPortAdapterOf(portWrapper, properties);

        drivingAdapter.register(portWrapperInstance);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    public void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> portAnnotation) {
        Validate.notNull(adapter);
        Validate.notNull(portAnnotation);

        //Create ports and adapter
        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);

        var portList = portFactory.getInstanceOfPorts(portAnnotation, properties);
        portList.forEach(drivingAdapter::register);
        
        compositeDrivingAdapter.add(drivingAdapter);
    }

    public <T> T newInstanceOfPort(Class<T> port)
    {
        return port.cast(portFactory.newInstanceOf(port, properties));
    }

    @SuppressWarnings("unused")
    public <T> T getInstanceOfPort(Class<T> port)
    {
        return port.cast(portFactory.getInstanceOf(port, properties));
    }


    public void startDrivingAdapters()
    {
        compositeDrivingAdapter.start();
    }

    public void stopDrivingAdapters()
    {
        compositeDrivingAdapter.stop();
    }

    public BoundedContext getBoundedContext()
    {
        return boundedContext;
    }

    public void run()
    {
        setupSignalHandler();

        startDrivingAdapters();

        boundedContext.run();

        stopDrivingAdapters();
    }

    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JexxaLogger.getLogger(JexxaMain.class).info("Shutdown signal received ...");
            boundedContext.shutdown();
        }));
    }

}
