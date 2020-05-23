package io.jexxa.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.jexxa.core.factory.AdapterFactory;
import io.jexxa.core.factory.PortFactory;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

@SuppressWarnings("UnusedReturnValue")
public class JexxaMain
{

    public static final String JEXXA_APPLICATION_PROPERTIES = "/jexxa-application.properties";
    private static final String JEXXA_CONTEXT_NAME =  "io.jexxa.context.name";
    private static final String JEXXA_APPLICATION_CORE =  "io.jexxa.application";
    private static final String JEXXA_DRIVEN_ADAPTER = "io.jexxa.infrastructure.drivenadapter";

    private static final Logger LOGGER = JexxaLogger.getLogger(JexxaMain.class);

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

        loadJexxaProperties(this.properties);
        this.properties.putAll( properties );
        this.properties.put(JEXXA_CONTEXT_NAME, contextName);

        this.compositeDrivingAdapter = new CompositeDrivingAdapter();

        this.drivingAdapterFactory = new AdapterFactory();
        this.drivenAdapterFactory = new AdapterFactory();
        this.portFactory = new PortFactory(drivenAdapterFactory);

        addToInfrastructure(JEXXA_DRIVEN_ADAPTER);
        addToApplicationCore(JEXXA_APPLICATION_CORE);
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


    public <T> T getInstanceOfPort(Class<T> port)
    {
        return port.cast(portFactory.getInstanceOf(port, properties));
    }


    @SuppressWarnings("java:S2629")
    public BoundedContext start()
    {
        if ( !boundedContext.isRunning() )
        {
            LOGGER.info("Start BoundedContext '{}' with {} Driving Adapter ", getBoundedContext().contextName(), compositeDrivingAdapter.size());
            compositeDrivingAdapter.start();
            boundedContext.start();
            var startTime = getBoundedContext().uptime();
            LOGGER.info("BoundedContext '{}' successfully started in {}.{} seconds", getBoundedContext().contextName(), startTime.toSeconds(), startTime.toMillisPart());
        } else {
            LOGGER.warn("BoundedContext '{}' already started", getBoundedContext().contextName());
        }
        return boundedContext;
    }

    @SuppressWarnings("java:S2629")
    public void stop()
    {
        if ( boundedContext.isRunning() )
        {
            boundedContext.stop();
            compositeDrivingAdapter.stop();
            LOGGER.info("BoundedContext '{}' successfully stopped", getBoundedContext().contextName());
        }
    }

    public BoundedContext getBoundedContext()
    {
        return boundedContext;
    }

    public Properties getProperties()
    {
        return properties;
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

    JexxaMain bindToPortAdapter(Class<? extends IDrivingAdapter> adapter, Class<?> portWrapper)
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

    private void loadJexxaProperties(Properties properties)
    {
        if ( JexxaMain.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES) != null )
        {
            try
            {
                properties.load(JexxaMain.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES));
            }
            catch (IOException e)
            {
                LOGGER.error("Could not load properties file {}.", JEXXA_APPLICATION_PROPERTIES);
            }
        }
        else
        {
            LOGGER.warn("NO PROPERTIES FILE FOUND {}", JEXXA_APPLICATION_PROPERTIES);
        }

    }


    static class CompositeDrivingAdapter implements IDrivingAdapter
    {
        private final Set<IDrivingAdapter> drivingAdapters = new HashSet<>();

        @Override
        public void start()
        {
            drivingAdapters.forEach(ThrowingConsumer.exceptionLogger(IDrivingAdapter::start));
        }

        @Override
        public void stop()
        {
            drivingAdapters.forEach(ThrowingConsumer.exceptionLogger(IDrivingAdapter::stop));
        }

        @Override
        public void register(Object object)
        {
            Validate.notNull(object);
            drivingAdapters.forEach(element -> element.register(object));
        }

        public void add(IDrivingAdapter drivingAdapter)
        {
            Validate.notNull(drivingAdapter);
            drivingAdapters.add(drivingAdapter);
        }

        public int size()
        {
            return drivingAdapters.size();
        }

    }
}
