package io.jexxa.infrastructure.drivingadapter.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.function.ThrowingConsumer;

public class JMXAdapter implements IDrivingAdapter
{
    private final List<MBeanConvention> registeredMBeans = new ArrayList<>();
    private final Properties properties;


    public JMXAdapter(Properties properties)
    {
        this.properties = Objects.requireNonNull(properties);
    }

    public void register(Object object)
    {
        Objects.requireNonNull(object);

        var mbs = ManagementFactory.getPlatformMBeanServer();
        var mBeanConvention = new MBeanConvention(object, properties);

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
        var mbs = ManagementFactory.getPlatformMBeanServer();

        registeredMBeans.stream()
                .filter(element -> mbs.isRegistered(element.getObjectName()))
                .forEach(ThrowingConsumer.exceptionLogger(element ->  mbs.unregisterMBean(element.getObjectName())));
    }

}
