package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

public class JMXAdapter implements IDrivingAdapter
{
    private List<MBeanModel> registeredMBeans = new ArrayList<>();

    public JMXAdapter()
    {

    }

    public JMXAdapter(Properties properties)
    {
        //TODO handle properties 
    }
    
    public void register(Object object)
    {
        Validate.notNull(object);
        validateJMXSettings();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        MBeanModel mBeanModel = new MBeanModel(object);

        //Check if service is already registered 
        if (!mbs.queryMBeans(mBeanModel.getObjectName(), null).isEmpty())
        {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "> Object already registered : " + object.getClass().getSimpleName());
        }

        //register Service
        try
        {
            mbs.registerMBean(mBeanModel, mBeanModel.getObjectName());
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }

        registeredMBeans.add(mBeanModel);
    }

    @Override
    public void start()
    {
        /*
         * No special action needed because objects are already registered at registerObject
         */
    }

    @Override
    public void stop()
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        registeredMBeans.stream().
                filter(element -> mbs.isRegistered(element.getObjectName())).
                forEach(exceptionWrapper(element ->  mbs.unregisterMBean(element.getObjectName())));
    }


    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    private static <T> Consumer<T>
    exceptionWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try
            {
                throwingConsumer.accept(i);
            }
            catch (Exception e)
            {
                JexxaLogger.getLogger(JMXAdapter.class).warn(e.getMessage());
            }
        };
    }


    
    private void validateJMXSettings()
    {
        Validate.notNull(System.getProperty("com.sun.management.jmxremote.port"),
                "set com.sun.management.jmxremote.port");
        Validate.isTrue("false".equals(System.getProperty("com.sun.management.jmxremote.authenticate")),
                "set com.sun.management.jmxremote.authenticate=false");
        Validate.isTrue("false".equals(System.getProperty("com.sun.management.jmxremote.ssl")),
                "set com.sun.management.jmxremote.ssl=false.");
    }
}
