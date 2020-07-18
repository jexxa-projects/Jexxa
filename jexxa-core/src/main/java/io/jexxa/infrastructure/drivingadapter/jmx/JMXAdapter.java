package io.jexxa.infrastructure.drivingadapter.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang3.Validate;

public class JMXAdapter implements IDrivingAdapter
{
    private final List<MBeanConvention> registeredMBeans = new ArrayList<>();
    private final Properties properties;


    public JMXAdapter(Properties properties)
    {
        Validate.notNull(properties);
        this.properties = properties;
    }

    public void register(Object object)
    {
        Validate.notNull(object);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        MBeanConvention mBeanConvention = new MBeanConvention(object, properties);

        //Check if service is already registered 
        if (!mbs.queryMBeans(mBeanConvention.getObjectName(), null).isEmpty())
        {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "> Object already registered : " + object.getClass().getSimpleName());
        }

        //register Service
        try
        {
            mbs.registerMBean(mBeanConvention, mBeanConvention.getObjectName());
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e)
        {
            throw new IllegalArgumentException(e);
        }

        registeredMBeans.add(mBeanConvention);
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

        registeredMBeans.stream()
                .filter(element -> mbs.isRegistered(element.getObjectName()))
                .forEach(ThrowingConsumer.exceptionLogger(element ->  mbs.unregisterMBean(element.getObjectName())));
    }

}
