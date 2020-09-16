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
import io.jexxa.utils.annotations.CheckReturnValue;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import org.apache.commons.lang3.Validate;
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
public class JexxaMain
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
     * In addition the properties file jexxa-application.properties is load if available in class path.
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
        Validate.notNull(properties);
        Validate.notNull(contextName);

        this.boundedContext = new BoundedContext(contextName, this);

        loadProperties(this.properties);
        this.properties.putAll( properties );  //add/overwrite given properties
        this.properties.put(JEXXA_CONTEXT_NAME, contextName);

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
    @SuppressWarnings("unused")
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


    private void loadProperties(Properties properties)
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
                //In case of any error we stop all driving adapter for proper cleanup and rethrow the exception
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


    static class JexxaExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final JexxaMain jexxaMain;

        JexxaExceptionHandler(JexxaMain jexxaMain)
        {
            this.jexxaMain = jexxaMain;
        }

        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("\nCould not startup Jexxa! {}", e.getMessage());
            jexxaMain.stop();
        }
    }
}
