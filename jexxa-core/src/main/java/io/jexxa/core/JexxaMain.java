package io.jexxa.core;

import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;
import io.jexxa.core.factory.AdapterFactory;
import io.jexxa.core.factory.PortFactory;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.function.ThrowingConsumer;
import io.jexxa.utils.properties.JexxaCoreProperties;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

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
    private static final String DRIVEN_ADAPTER_PACKAGE = ".infrastructure.drivenadapter";
    private static final String DRIVING_ADAPTER_PACKAGE = ".infrastructure.drivingadapter";
    private static final String DOMAIN_SERVICE = ".domainservice";
    private static final String DOMAIN_PROCESS_SERVICE = ".domainprocessservice";
    private static final String APPLICATION_SERVICE = ".applicationservice";


    public static final String JEXXA_APPLICATION_PROPERTIES = "/jexxa-application.properties";

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
    public JexxaMain(Class<?> context)
    {
        this(context.getSimpleName(), System.getProperties());
    }
    public JexxaMain(Class<?> context, Properties properties)
    {
        this(context.getSimpleName(), properties);
    }


    /**
     * Creates the JexxaMain instance for your application with given context name.
     *
     * Note: The file jexxa-application.properties is loaded if available in class path. Then the
     * properties are extended by the given properties object. So if you define the same properties in
     * jexxa-application.properties and the given properties object, the one from properties object is used.
     *
     * @param contextName Name of the BoundedContext. Typically, you should use the name of your application.
     * @param applicationProperties Properties that are defined by your application.
     */
    public JexxaMain(String contextName, Properties applicationProperties)
    {
        Objects.requireNonNull(applicationProperties);
        Objects.requireNonNull(contextName);

        // Handle properties in following forder:
        // 0. Add default JEXXA_CONTEXT_MAIN
        this.properties.put(JexxaCoreProperties.JEXXA_CONTEXT_NAME, contextName);

        // 1. Load properties from application.properties because they have the lowest priority
        loadJexxaApplicationProperties(this.properties);
        // 2. Use System properties because they have mid-priority
        this.properties.putAll( System.getProperties() );  //add/overwrite system properties
        // 3. Use given properties because they have the highest priority
        this.properties.putAll( applicationProperties );  //add/overwrite given properties
        // 4. import properties that are defined by '"io.jexxa.config.import"'
        if( this.properties.containsKey(JexxaCoreProperties.JEXXA_CONFIG_IMPORT) )
        {
            importProperties(this.properties.getProperty(JexxaCoreProperties.JEXXA_CONFIG_IMPORT));
        }

        this.addToInfrastructure("io.jexxa.infrastructure.drivingadapter");

        //Create BoundedContext
        this.boundedContext = new BoundedContext(this.properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_NAME), this);

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
     * This method adds default package structure as recommended by Jexxa. In case you use your own package structure
     * see {@link #addToInfrastructure(String)} and {@link #addToApplicationCore(String)}.
     * The default structure added to {@link #addToApplicationCore(String)} is:
     *
     * <ul>
     *     <li>&lt;Root-Package&gt;.applicationservice</li>
     *     <li>&lt;Root-Package&gt;.domainservice</li>
     *     <li>&lt;Root-Package&gt;.domainprocessservice</li>
     * </ul>
     * The default structure which is added to {@link #addToInfrastructure(String)} is:
     *
     * <ul>
     *     <li>&lt;Root-Package&gt;.infrastructure.drivenadapter</li>
     *     <li>&lt;Root-Package&gt;infrastructure.drivingadapter</li>
     * </ul>
     *
     * @param mainApplication which is located at the root package. From this package name the remaining packages are added
     * @return JexxaMain object to call additional methods
     */
    public JexxaMain addDDDPackages(Class<?> mainApplication)
    {
        addToInfrastructure( mainApplication.getPackageName() + DRIVEN_ADAPTER_PACKAGE);
        addToInfrastructure( mainApplication.getPackageName() + DRIVING_ADAPTER_PACKAGE);
        addToApplicationCore( mainApplication.getPackageName() + DOMAIN_SERVICE);
        addToApplicationCore( mainApplication.getPackageName() + DOMAIN_PROCESS_SERVICE);
        addToApplicationCore( mainApplication.getPackageName() + APPLICATION_SERVICE);

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
    public <T> FluentInterceptor intercept(Class<T> clazz)
    {
        if (AdapterConvention.isPortAdapter(clazz, getInfrastructure())) {
            return new FluentInterceptor(this, portFactory.getPortAdapterOf(clazz, getProperties()));
        }
        return new FluentInterceptor(this, getInstanceOfInboundPort(clazz));
    }

    @CheckReturnValue
    public <T> FluentInterceptor intercept(T object)
    {
        return new FluentInterceptor(this, object);
    }

    @CheckReturnValue
    public FluentInterceptor interceptAnnotation(Class<? extends Annotation> portAnnotation)
    {
        var targetObjects = portFactory
                .getAnnotatedPorts(portAnnotation)
                .stream()
                .map(this::getInstanceOfPort)
                .toArray();

        return new FluentInterceptor(this, targetObjects);
    }

    @CheckReturnValue
    public FluentMonitor monitor(Class<?> targetObject)
    {
        if (AdapterConvention.isPortAdapter(targetObject, getInfrastructure()))
        {
            return new FluentMonitor(this, portFactory.getPortAdapterOf(targetObject, properties));
        }

        return new FluentMonitor(this, portFactory.getInstanceOf(targetObject, properties));
    }

    @CheckReturnValue
    public JexxaMain registerHealthCheck(HealthCheck healthCheck)
    {
        boundedContext.registerHealthCheck(healthCheck);
        return this;
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
        LOGGER.info( getBoundedContext().getJexxaVersion().toString());

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

    public void importProperties(String resource)
    {
        Optional.ofNullable(JexxaMain.class.getResourceAsStream(resource))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load),
                        () -> {throw new IllegalArgumentException("Properties file " + resource + " not available. Please check the filename!");}
                );

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

            Throwable rootCause = e;
            Throwable rootCauseWithMessage = null ;

            while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
            {
                if ( rootCause.getMessage() != null && !rootCause.getMessage().isEmpty())
                {
                    rootCauseWithMessage = rootCause;
                }

                rootCause = rootCause.getCause();
            }

            var detailedMessage = ""; // Create a potential reason in from of "lastMessage -> lastException" or just "lastMessage"
            if (rootCauseWithMessage != null && rootCauseWithMessage != rootCause)
            {
                detailedMessage = rootCauseWithMessage.getClass().getSimpleName() + ": " + rootCauseWithMessage.getMessage() + " -> Exception: " + rootCause.getClass().getSimpleName();
            } else {
                detailedMessage = rootCause.getMessage();
            }

            stringBuilder.append("\n* Jexxa-Message    : ").append(jexxaMessage);
            stringBuilder.append("\n* Detailed-Message : ").append(detailedMessage);
            stringBuilder.append("\n* 1st trace element: ").append( rootCause.getStackTrace()[0] );

            return stringBuilder.toString();
        }
    }
}
