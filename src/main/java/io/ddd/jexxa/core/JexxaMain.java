package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

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

        this.boundedContext = new BoundedContext(contextName, this);
        this.properties.putAll( properties );
        this.properties.put("io.ddd.jexxa.context.name", contextName);

        this.compositeDrivingAdapter = new CompositeDrivingAdapter();

        this.drivingAdapterFactory = new AdapterFactory();
        this.drivenAdapterFactory = new AdapterFactory();
        this.portFactory = new PortFactory(drivenAdapterFactory);

        addToInfrastructure("io.ddd.jexxa.infrastructure.drivenadapter");
        addToApplicationCore("io.ddd.jexxa.application");
    }

    public JexxaMain addToInfrastructure(String packageName)
    {
        drivenAdapterFactory.whiteListPackage(packageName);
        return this;
    }

    public JexxaMain addToApplicationCore(String packageName)
    {
        portFactory.whiteListPackage(packageName);
        return this;
    }

    public <T, K> K addBootstrapService(Class<T> bootstrapService, Function< T, K > initFunction)
    {
        T instance = portFactory.getInstanceOf(bootstrapService, properties);
        return initFunction.apply(instance);
    }

    public <T> BootstrapService<T> bootstrap(Class<T> bootstrapService)
    {
        return new BootstrapService<>(bootstrapService, this);
    }

    public <T extends IDrivingAdapter> DrivingAdapter<T>  bind(Class<T> clazz)
    {
        return new DrivingAdapter<>(clazz, this);
    }

    public <T> T newInstanceOfPort(Class<T> port)
    {
        return port.cast(portFactory.newInstanceOf(port, properties));
    }

    public <T> T getInstanceOfPort(Class<T> port)
    {
        return port.cast(portFactory.getInstanceOf(port, properties));
    }


    public JexxaMain start()
    {
        setupSignalHandler();
        compositeDrivingAdapter.start();
        boundedContext.start();
        return this;
    }

    public void stop()
    {
        compositeDrivingAdapter.stop();
        boundedContext.stop();
    }

    public BoundedContext getBoundedContext()
    {
        return boundedContext;
    }
    
    JexxaMain bindToPort(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        var inboundPort    = portFactory.getInstanceOf(port, properties);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
        return this;
    }

    JexxaMain bindToPort(Class<? extends IDrivingAdapter> adapter, Object port) {
        Validate.notNull(adapter);
        Validate.notNull(port);

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        drivingAdapter.register(port);

        compositeDrivingAdapter.add(drivingAdapter);

        return this;
    }

    JexxaMain bindToPortWrapper(Class<? extends IDrivingAdapter> adapter, Class<?> portWrapper)
    {
        var drivingAdapter = drivingAdapterFactory.newInstanceOf(adapter, properties);

        var portWrapperInstance = portFactory.getPortAdapterOf(portWrapper, properties);

        drivingAdapter.register(portWrapperInstance);

        compositeDrivingAdapter.add(drivingAdapter);

        return this;
    }

    JexxaMain bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> portAnnotation) {
        Validate.notNull(adapter);
        Validate.notNull(portAnnotation);

        //Create ports and adapter
        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);

        var portList = portFactory.getInstanceOfPorts(portAnnotation, properties);
        portList.forEach(drivingAdapter::register);
        
        compositeDrivingAdapter.add(drivingAdapter);

        return this;
    }
    <T> JexxaMain addBootstrapService(Class<T> bootstrapService, Consumer<T> initFunction)
    {
        T instance = portFactory.getInstanceOf(bootstrapService, properties);
        initFunction.accept(instance);
        return this;
    }



    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JexxaLogger.getLogger(JexxaMain.class).info("Shutdown signal received ...");
            boundedContext.shutdown();
        }));
    }

    synchronized void notifyShutdown()
    {
        this.notifyAll();
    }

    public synchronized JexxaMain waitForShutdown()
    {
        try
        {
            while ( boundedContext.isRunning() ) {
                this.wait();
            }
        }
        catch (Exception e)
        {
            JexxaLogger.getLogger(this.getClass()).error(e.getMessage());
        }

        return this;
    }

}
