package io.jexxa.core;

import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.core.convention.AdapterConvention;
import io.jexxa.core.convention.PortConvention;
import io.jexxa.core.factory.AdapterFactory;
import io.jexxa.core.factory.PortFactory;
import io.jexxa.utils.JexxaBanner;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.function.ThrowingConsumer;
import io.jexxa.utils.properties.JexxaCoreProperties;
import io.jexxa.utils.properties.PropertiesLoader;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static io.jexxa.utils.JexxaBanner.addConfigBanner;

/**
 * JexxaMain is the main entry point for your application to use Jexxa. Within each application only a single instance
 * of this class must exist.
 * <p>
 * In order to control your application (start / shutdown) from within your application and also from outside
 * JexxaMain provides a so called {@link BoundedContext}.
 * <p>
 * To see how to use this class please refer to the tutorials.
 */
@SuppressWarnings("unused")
public final class JexxaMain
{
    private static final String DRIVEN_ADAPTER_PACKAGE = ".infrastructure.drivenadapter";
    private static final String DRIVING_ADAPTER_PACKAGE = ".infrastructure.drivingadapter";
    private static final String DOMAIN_SERVICE = ".domainservice";
    private static final String DOMAIN_PROCESS_SERVICE = ".domainprocessservice";
    private static final String DOMAIN_WORKFLOW = ".domainworkflow";
    private static final String APPLICATION_SERVICE = ".applicationservice";

    private final CompositeDrivingAdapter compositeDrivingAdapter = new CompositeDrivingAdapter();
    private final Properties properties;
    private final AdapterFactory drivingAdapterFactory  = new AdapterFactory();
    private final AdapterFactory drivenAdapterFactory   = new AdapterFactory();
    private final PortFactory portFactory               = new PortFactory(drivenAdapterFactory);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final BoundedContext boundedContext;

    private final PropertiesLoader propertiesLoader;

    private boolean enableBanner = true;

    /**
     * Creates the JexxaMain instance for your application with given context name.
     * In addition, the properties file jexxa-application.properties is load if available in class path.
     * <p>
     * Note: When a driving or driven adapter is created, it gets the properties read from properties file.
     *
     * @param context Name of the BoundedContext. Typically, you should use the name of your application.
     */
    public JexxaMain(Class<?> context)
    {
        this(context, System.getProperties());
    }
    /**
     * Creates the JexxaMain instance for your application with given context name.
     * <p>
     * Note: The file jexxa-application.properties is loaded if available in class path. Then the
     * properties are extended by the given properties object. So if you define the same properties in
     * jexxa-application.properties and the given properties object, the one from properties object is used.
     *
     * @param context Type of the BoundedContext. Typically, you should use the name of your application.
     * @param applicationProperties Properties that are defined by your application.
     */
    public JexxaMain(Class<?> context, Properties applicationProperties)
    {
        Objects.requireNonNull(applicationProperties);
        Objects.requireNonNull(context);

        // Handle properties in following forder:
        // 0. Add default JEXXA_CONTEXT_MAIN
        this.propertiesLoader = new PropertiesLoader(context);
        this.properties = propertiesLoader.createJexxaProperties(applicationProperties);
        this.properties.put(JexxaCoreProperties.JEXXA_CONTEXT_NAME, context.getSimpleName());

        this.addToInfrastructure("io.jexxa.infrastructure.drivingadapter");
        this.addDDDPackages(context);

        //Create BoundedContext
        this.boundedContext = new BoundedContext(this.properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_NAME), this);

        setExceptionHandler();
        addConfigBanner(this::printStartupInfo);
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
        addToApplicationCore( mainApplication.getPackageName() + DOMAIN_WORKFLOW);
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

    public JexxaMain bootstrapAnnotation(Class<? extends Annotation> annotation)
    {
        var inboundPorts = portFactory.getAnnotatedPorts(annotation);
        inboundPorts.forEach(PortConvention::validate);

        inboundPorts
                .forEach(element -> portFactory.getInstanceOf(element, properties));

        return this;
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
    @SuppressWarnings("unused")
    public JexxaMain logUnhealthyDiagnostics(int period, TimeUnit timeUnit)
    {
        executorService.scheduleAtFixedRate(this::logUnhealthyDiagnostics,0, period,timeUnit);

        return this;
    }

    private void logUnhealthyDiagnostics()
    {
        if (getBoundedContext().isRunning() && !getBoundedContext().isHealthy())
        {
            getBoundedContext()
                    .diagnostics()
                    .stream()
                    .filter(element -> !element.isHealthy())
                    .forEach(element -> JexxaLogger.getLogger(JexxaMain.class).error(element.statusMessage()));
        }
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

    public JexxaMain disableBanner()
    {
        enableBanner = false;
        return this;
    }

    /**
     * This convenience method invokes the three main control methods
     *  start() -> waitForShutdown() -> stop
     */
    public void run()
    {
        start();
        waitForShutdown();
        stop();
    }

    @SuppressWarnings("java:S2629")
    public JexxaMain start()
    {
        if ( boundedContext.isRunning() )
        {
            JexxaLogger.getLogger(JexxaMain.class).warn("BoundedContext '{}' already started", getBoundedContext().contextName());
            return this;
        }

        if (enableBanner)
        {
            JexxaBanner.show(getProperties());
        }

        compositeDrivingAdapter.start();
        boundedContext.start();

        printStartupDuration();

        return this;
    }

    @SuppressWarnings("java:S2629")
    void printStartupInfo(Properties properties)
    {
        JexxaLogger.getLogger(JexxaBanner.class).info( "Jexxa Version                  : {}", getBoundedContext().jexxaVersion() );
        JexxaLogger.getLogger(JexxaBanner.class).info( "Context Version                : {}", getBoundedContext().contextVersion() );

        JexxaLogger.getLogger(JexxaBanner.class).info( "Used Driving Adapter           : {}", Arrays.toString(compositeDrivingAdapter.adapterNames().toArray()));
        JexxaLogger.getLogger(JexxaBanner.class).info( "Used Properties Files          : {}", Arrays.toString(propertiesLoader.getPropertiesFiles().toArray()));
    }

    @SuppressWarnings("java:S2629")
    void printStartupDuration()
    {
        var startTime = getBoundedContext().uptime();
        JexxaLogger.getLogger(JexxaMain.class).info("BoundedContext '{}' successfully started in {}.{} seconds", getBoundedContext().contextName(), startTime.toSeconds(), String.format("%03d", startTime.toMillisPart()));
    }

    @SuppressWarnings("java:S2629")
    public void stop()
    {
        if ( boundedContext.isRunning() )
        {
            boundedContext.stop();
            compositeDrivingAdapter.stop();
            JexxaLogger.getLogger(JexxaMain.class).info("BoundedContext '{}' successfully stopped", getBoundedContext().contextName());
        }
        executorService.shutdown();
    }

    @SuppressWarnings("UnusedReturnValue")
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

    /**
     * Registers a driven adapter that is then injected inbound ports if its interface is requested.
     * <p>
     * Note: This method is typically only required if you need to define the created instances explicitly,
     * such as defining stubs in unit tests.
     *
     * @param drivenAdapter that should be explicitly used for Jexxa's dependency injection
     * @param <T> type of the driven adapter
     */
    public <T> void registerDrivenAdapter(Class<T> drivenAdapter)
    {
        drivenAdapterFactory.getInstanceOf(drivenAdapter, getProperties());
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

    PortFactory getPortFactory()
    {
        return portFactory;
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
        public List<String> adapterNames()
        {
            return drivingAdapters.stream().map( element -> element.getClass().getSimpleName()).toList();
        }
    }


    record JexxaExceptionHandler(JexxaMain jexxaMain) implements Thread.UncaughtExceptionHandler {

        public void uncaughtException(Thread t, Throwable e) {
            var errorMessage = getOutputMessage(e);
            //Show startup banner if enabled and jexxa not started in order to
            if ( jexxaMain.enableBanner &&
                !jexxaMain.getBoundedContext().isRunning())
            {
                JexxaBanner.show(jexxaMain.getProperties());
            }

            JexxaLogger.getLogger(JexxaMain.class).error("Could not startup Jexxa! {}", errorMessage);

            jexxaMain.stop();
        }

        String getOutputMessage(Throwable e) {
            var stringBuilder = new StringBuilder();
            var jexxaMessage = e.getMessage();

            Throwable rootCause = e;
            Throwable rootCauseWithMessage = null;

            while (rootCause.getCause() != null && !rootCause.getCause().equals(rootCause)) {
                if (rootCause.getMessage() != null && !rootCause.getMessage().isEmpty()) {
                    rootCauseWithMessage = rootCause;
                }

                rootCause = rootCause.getCause();
            }

            var detailedMessage = ""; // Create a potential reason in from of "lastMessage -> lastException" or just "lastMessage"
            if (rootCauseWithMessage != null && !rootCauseWithMessage.equals(rootCause)) {
                detailedMessage = rootCauseWithMessage.getClass().getSimpleName() + ": " + rootCauseWithMessage.getMessage() + " -> Exception: " + rootCause.getClass().getSimpleName();
            } else {
                detailedMessage = rootCause.getMessage();
            }

            stringBuilder.append("\n* Jexxa-Message    : ").append(jexxaMessage);
            stringBuilder.append("\n* Detailed-Message : ").append(detailedMessage);
            stringBuilder.append("\n* 1st trace element: ").append(rootCause.getStackTrace()[0]);

            return stringBuilder.toString();
        }
    }


}
