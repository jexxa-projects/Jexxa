package io.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.jexxa.core.convention.PortConvention;
import io.jexxa.core.factory.AdapterFactory;
import io.jexxa.core.factory.PortFactory;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.CheckReturnValue;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class JexxaMain
{

    public static final String JEXXA_APPLICATION_PROPERTIES = "/jexxa-application.properties";
    private static final String JEXXA_CONTEXT_NAME =  "io.jexxa.context.name";

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
        setExceptionHandler();
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

    @CheckReturnValue
    public <T, K> K addBootstrapService(Class<T> bootstrapService, Function< T, K > initFunction)
    {
        T instance = portFactory.getInstanceOf(bootstrapService, properties);
        return initFunction.apply(instance);
    }

    @CheckReturnValue
    public <T> BootstrapService<T> bootstrap(Class<T> bootstrapService)
    {
        return new BootstrapService<>(bootstrapService, this);
    }

    @CheckReturnValue
    public <T extends IDrivingAdapter> DrivingAdapter<T>  bind(Class<T> clazz)
    {
        return new DrivingAdapter<>(clazz, this);
    }

    @CheckReturnValue
    public <T> T getInstanceOfPort(Class<T> port)
    {
        PortConvention.validate(port);
        return port.cast(portFactory.getInstanceOf(port, properties));
    }

    @CheckReturnValue
    public <T> T getDrivingAdapter(Class<T> adapter)
    {
        return drivingAdapterFactory.getInstanceOf(adapter, getProperties());
    }

    @SuppressWarnings("java:S2629")
    public BoundedContext start()
    {
        if ( boundedContext.isRunning() )
        {
            LOGGER.warn("BoundedContext '{}' already started", getBoundedContext().contextName());
            return boundedContext;
        }
        
        LOGGER.info("Start BoundedContext '{}' with {} Driving Adapter ", getBoundedContext().contextName(), compositeDrivingAdapter.size());
        compositeDrivingAdapter.start();
        boundedContext.start();
        var startTime = getBoundedContext().uptime();
        LOGGER.info("BoundedContext '{}' successfully started in {}.{} seconds", getBoundedContext().contextName(), startTime.toSeconds(), startTime.toMillisPart());

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

    @CheckReturnValue
    public BoundedContext getBoundedContext()
    {
        return boundedContext;
    }

    @CheckReturnValue
    public Properties getProperties()
    {
        return properties;
    }
    
    protected void bindToPort(Class<? extends IDrivingAdapter> adapter, Class<?> port)
    {

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        var inboundPort    = portFactory.getInstanceOf(port, properties);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    protected JexxaMain bindToPort(Class<? extends IDrivingAdapter> adapter, Object port)
    {

        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);
        drivingAdapter.register(port);

        compositeDrivingAdapter.add(drivingAdapter);

        return this;
    }

    protected void bindToPortAdapter(Class<? extends IDrivingAdapter> adapter, Class<?> portWrapper)
    {
        var drivingAdapter = drivingAdapterFactory.newInstanceOf(adapter, properties);

        var portWrapperInstance = portFactory.getPortAdapterOf(portWrapper, properties);

        drivingAdapter.register(portWrapperInstance);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    protected void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> portAnnotation) {
        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);

        var portList = portFactory.getInstanceOfPorts(portAnnotation, properties);
        portList.forEach(drivingAdapter::register);
        
        compositeDrivingAdapter.add(drivingAdapter);
    }

    protected <T> void addBootstrapService(Class<T> bootstrapService, Consumer<T> initFunction)
    {
        T instance = portFactory.getInstanceOf(bootstrapService, properties);
        initFunction.accept(instance);
    }


    private void loadJexxaProperties(Properties properties)
    {
        Optional.ofNullable(JexxaMain.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load),
                        () -> LOGGER.warn("NO PROPERTIES FILE FOUND {}", JEXXA_APPLICATION_PROPERTIES)
                );
        
    }

    private void setExceptionHandler()
    {
        Optional.ofNullable(Thread.getDefaultUncaughtExceptionHandler())
                .ifPresentOrElse(
                        value -> LOGGER.warn("Uncaught Exception Handler already set => Don't register Jexxa's uncaught exception handler"),
                        () -> Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler())
                );

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


    static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("\nCould not startup Jexxa! {}", e.getMessage());
        }
    }
}
