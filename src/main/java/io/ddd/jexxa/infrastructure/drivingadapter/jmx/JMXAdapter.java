package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.jexxa.infrastructure.stereotype.DrivingAdapter;
import org.apache.commons.lang.Validate;

@DrivingAdapter
public class JMXAdapter implements IDrivingAdapter
{
    public void registerObject(Object object)
    {
        validateJMXSettings();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        MBeanModel mBeanModel = new MBeanModel(object);

        try
        {
            mbs.registerMBean(mBeanModel, mBeanModel.getObjectName());
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void start()
    {
        
    }

    @Override
    public void stop()
    {

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
