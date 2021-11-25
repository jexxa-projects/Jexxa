package io.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import io.jexxa.core.convention.PortConvention;
import io.jexxa.core.factory.AdapterFactory;
import io.jexxa.core.factory.PortFactory;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.function.ThrowingConsumer;
import org.slf4j.Logger;

/**
 * JexxaMain is the main entry point for your application to use Jexxa. Within each application only a single instance
 * of this class must exist.
 *
 * In order to control your application (start / shutdown) from within your application and also from outside
 * JexxaMain provides a so called {@link BoundedContext}.
 *
 * To see how to use this class please refer to the tutorials.
 */
public final class JexxaMain
{
    public static final String JEXXA_APPLICATION_PROPERTIES = "/jexxa-application.properties";
    private static final String JEXXA_CONTEXT_NAME =  "io.jexxa.context.name";

    private static final Logger LOGGER = JexxaLogger.getLogger(JexxaMain.class);

    private final CompositeDrivingAdapter compositeDrivingAdapter = new CompositeDrivingAdapter();
    private final Properties properties                 = new Properties();
    private final AdapterFactory drivingAdapterFactory  = new AdapterFactory();
    private final AdapterFactory drivenAdapterFactory   = new AdapterFactory();
    private final PortFactory portFactory               = new PortFactory(drivenAdapterFactory);

    private final BoundedContext boundedContext;

    /**
     * Creates the JexxaMain instance for your application with given context name.
     * In addition, the properties file jexxa-application.properties is load if available in class path.
     *
     * Note: When a driving or driven adapter is created, it gets the properties read from properties file.
     *
     * @param contextName Name of the BoundedContext. Typically, you should use the name of your application.
     */
    public JexxaMain(String contextName)
    {
        this(contextName, System.getProperties());
    }

    /**
     * Creates the JexxaMain instance for your application with given context name.
     *
     * Note: The file jexxa-application.properties is loaded if available in class path. Then the
     * properties are extended by the given properties object. So if you define the same properties in
     * jexxa-application.properties and the given properties object, the one from properties object is used.
     *
     * @param contextName Name of the BoundedContext. Typically, you should use the name of your application.
     * @param properties Properties that are defined by your application.
     */
    public JexxaMain(String contextName, Properties properties)
    {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(contextName);

        this.boundedContext = new BoundedContext(contextName, this);

        // Handle properties in following forder:
        // 1. Load properties from application.properties because they have the lowest priority
        loadJexxaApplicationProperties(this.properties);
        // 2. Use System properties because they have mid-priority
        this.properties.putAll( System.getProperties() );  //add/overwrite system properties
        // 3. Use given properties because they have the highest priority
        this.properties.putAll( properties );  //add/overwrite given properties

        this.properties.put(JEXXA_CONTEXT_NAME, contextName);
        this.addToInfrastructure("io.jexxa.infrastructure.drivingadapter");

        setExceptionHandler();
    }

    /**
     * Adds a package that is searched by Jexxa's dependency injection mechanism for creating infrastructure
     * objects such as driven adapters.
     * @param packageName name of the package
     * @return JexxaMain object to call additional methods
     */
    public JexxaMain addToInfrastructure(String packageName)
    {
        drivenAdapterFactory.acceptPackage(packageName);
        return this;
    }

    /**
     * Adds a package that is searched by Jexxa's dependency injection mechanism for creating objects of the
     * application core such as in- and outbound ports.
     *
     * @param packageName name of the package
     * @return JexxaMain object to call additional methods
     */
    public JexxaMain addToApplicationCore(String packageName)
    {
        portFactory.acceptPackage(packageName);
        return this;
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
    public <T extends IDrivingAdapter> DrivingAdapter<T>  conditionalBind(BooleanSupplier conditional, Class<T> clazz)
    {
        return new DrivingAdapter<>(conditional, clazz, this);
    }

    /**
     * Returns an instance of a Port and creates one if it not already exist.
     *
     * @param port Class information of the port. In case of an interface Jexxa tries to create an outbound port otherwise an inbound port
     * @param <T> Type of the port.
     * @return Instance of requested port. If an instance already exist it is returned otherwise a new one is created.
     */
    @CheckReturnValue
    public <T> T getInstanceOfPort(Class<T> port)
    {
        if ( port.isInterface() )
        {
            return getInstanceOfOutboundPort(port);
        }

        return getInstanceOfInboundPort(port);
    }

    @CheckReturnValue
    public <T> T getDrivingAdapter(Class<T> adapter)
    {
        return drivingAdapterFactory.getInstanceOf(adapter, getProperties());
    }

    @SuppressWarnings("java:S2629")
    public JexxaMain start()
    {
        if ( boundedContext.isRunning() )
        {
            LOGGER.warn("BoundedContext '{}' already started", getBoundedContext().contextName());
            return this;
        }

        printStartupInfo();

        compositeDrivingAdapter.start();
        boundedContext.start();

        printStartupDuration();

        return this;
    }

    @SuppressWarnings("java:S2629")
    void printStartupInfo()
    {
        LOGGER.info("{} {}; built: {}; git: {};"
                , JexxaVersion.PROJECT_NAME
                ,JexxaVersion.VERSION
                ,JexxaVersion.BUILD_TIMESTAMP
                ,JexxaVersion.REPOSITORY);


        LOGGER.info("Start BoundedContext '{}' with {} Driving Adapter ", getBoundedContext().contextName(), compositeDrivingAdapter.size());
    }

    @SuppressWarnings("java:S2629")
    void printStartupDuration()
    {
        var startTime = getBoundedContext().uptime();
        LOGGER.info("BoundedContext '{}' successfully started in {}.{} seconds", getBoundedContext().contextName(), startTime.toSeconds(), String.format("%03d", startTime.toMillisPart()));
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

    public JexxaMain waitForShutdown()
    {
        return getBoundedContext().waitForShutdown();
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

    List<String> getInfrastructure()
    {
        return drivenAdapterFactory.getAcceptPackages();
    }
    @SuppressWarnings("unused")
    List<String> getApplicationCore() { return portFactory.getAcceptPackages(); }

    void bindToPort(Class<? extends IDrivingAdapter> drivingAdapter, Class<?> inboundPort)
    {
        var drivingAdapterInstance = drivingAdapterFactory.getInstanceOf(drivingAdapter, properties);
        var inboundPortInstance    = portFactory.getInstanceOf(inboundPort, properties);
        Objects.requireNonNull(inboundPortInstance);
        drivingAdapterInstance.register(inboundPortInstance);

        compositeDrivingAdapter.add(drivingAdapterInstance);
    }

    JexxaMain bindToPort(Class<? extends IDrivingAdapter> drivingAdapter, Object inboundPort)
    {
        var drivingAdapterInstance = drivingAdapterFactory.getInstanceOf(drivingAdapter, properties);
        drivingAdapterInstance.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapterInstance);

        return this;
    }

    void bindToPortAdapter(Class<? extends IDrivingAdapter> drivingAdapter, Class<?> portAdapter)
    {
        var drivingAdapterInstance = drivingAdapterFactory.getInstanceOf(drivingAdapter, properties);

        var portAdapterInstance = portFactory.getPortAdapterOf(portAdapter, properties);

        drivingAdapterInstance.register(portAdapterInstance);

        compositeDrivingAdapter.add(drivingAdapterInstance);
    }

    void bindToAnnotatedPorts(Class<? extends IDrivingAdapter> adapter, Class<? extends Annotation> portAnnotation)
    {
        var drivingAdapter = drivingAdapterFactory.getInstanceOf(adapter, properties);

        var inboundPorts = portFactory.getAnnotatedPorts(portAnnotation);
        inboundPorts.forEach(PortConvention::validate);

        inboundPorts.stream()
                .map(element -> portFactory.getInstanceOf(element, properties))
                .forEach(drivingAdapter::register);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    <T> void addBootstrapService(Class<T> bootstrapService, Consumer<T> initFunction)
    {
        var instance = portFactory.getInstanceOf(bootstrapService, properties);
        initFunction.accept(instance);
    }


    private <T> T getInstanceOfInboundPort(Class<T> port)
    {
        PortConvention.validate(port);
        return port.cast(portFactory.getInstanceOf(port, properties));
    }

    private <T> T getInstanceOfOutboundPort(Class<T> port)
    {
        return drivenAdapterFactory.getInstanceOf(port, properties);
    }


    private void loadJexxaApplicationProperties(Properties properties)
    {
        Optional.ofNullable(JexxaMain.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load),
                        () -> LOGGER.warn("NO PROPERTIES FILE FOUND {}", JEXXA_APPLICATION_PROPERTIES)
                );

    }

    private void setExceptionHandler()
    {
        if (Thread.getDefaultUncaughtExceptionHandler() == null)
        {
            Thread.setDefaultUncaughtExceptionHandler(new JexxaExceptionHandler(this));
        }
    }

    public void addProperties(Properties properties)
    {
        this.properties.putAll(properties);
    }

    /**
     * CompositeDrivingAdapter starts all registered IDrivingAdapter
     * In case of a failure starting a single IDrivingAdapter all registered and already started IDrivingAdapter are stopped
     */
    static class CompositeDrivingAdapter implements IDrivingAdapter
    {
        private final Set<IDrivingAdapter> drivingAdapters = new HashSet<>();

        @Override
        public void start()
        {
            try {
                drivingAdapters.forEach(IDrivingAdapter::start);
            }
            catch (RuntimeException e)
            {
                //In case of any error we stop ALL driving adapter for proper cleanup and rethrow the exception
                stop();
                throw e;
            }
        }

        @Override
        public void stop()
        {
            drivingAdapters.forEach(ThrowingConsumer.exceptionLogger(IDrivingAdapter::stop));
        }

        @Override
        public void register(Object object)
        {
            Objects.requireNonNull(object);
            drivingAdapters.forEach(element -> element.register(object));
        }

        public void add(IDrivingAdapter drivingAdapter)
        {
            Objects.requireNonNull(drivingAdapter);
            drivingAdapters.add(drivingAdapter);
        }

        public int size()
        {
            return drivingAdapters.size();
        }
    }


    static class JexxaExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final JexxaMain jexxaMain;

        JexxaExceptionHandler(JexxaMain jexxaMain)
        {
            this.jexxaMain = jexxaMain;
        }

        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Could not startup Jexxa! {}", getOutputMessage(e));

            jexxaMain.stop();
        }

        String getOutputMessage( Throwable e)
        {
            var stringBuilder = new StringBuilder();
            var jexxaMessage = e.getMessage();
            var detailedMessage = "Not available";
            if (e.getCause() != null && e.getCause().getMessage() != null)
            {
                detailedMessage = e.getCause().getMessage();
            }

            stringBuilder.append("\n* Jexxa-Message: ").append(jexxaMessage);
            stringBuilder.append("\n* Detailed-Message: ").append(detailedMessage);

            return stringBuilder.toString();
        }
    }
}
