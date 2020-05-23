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
import org.apache.commons.lang.Validate;

public class JMXAdapter implements IDrivingAdapter
{
    private final List<MBeanModel> registeredMBeans = new ArrayList<>();
    private final Properties properties;


    public JMXAdapter()
    {
        this(null);
    }

    public JMXAdapter(Properties properties)
    {
        this.properties = properties;
    }

    public void register(Object object)
    {
        Validate.notNull(object);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        MBeanModel mBeanModel = new MBeanModel(object, properties);

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
            throw new IllegalArgumentException(e);
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

        registeredMBeans.stream()
                .filter(element -> mbs.isRegistered(element.getObjectName()))
                .forEach(ThrowingConsumer.exceptionLogger(element ->  mbs.unregisterMBean(element.getObjectName())));
    }
    
}
