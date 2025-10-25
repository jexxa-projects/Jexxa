package io.jexxa.jexxatest;

import io.jexxa.core.JexxaMain;
import io.jexxa.jexxatest.integrationtest.messaging.JMSBinding;
import io.jexxa.jexxatest.integrationtest.rest.RESTBinding;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jexxatest.JexxaTest.loadJexxaTestProperties;
import static org.awaitility.Awaitility.await;

public class JexxaIntegrationTest
{
    private final Properties properties;
    private final Class<?> application;
    private RESTBinding restBinding;
    private JMSBinding messageBinding;
    private final JexxaMain jexxaMain;
    private final Map<Class<?>, Object> bindingMap = new HashMap<>();

    public JexxaIntegrationTest(Class<?> application)
    {
        this.application = application;
        jexxaMain = new JexxaMain(application);
        jexxaMain.addProperties( loadJexxaTestProperties() );
        this.properties = jexxaMain.getProperties();
    }

    public RESTBinding getRESTBinding()
    {
        if (restBinding == null)
        {
            restBinding = new RESTBinding(getProperties());
            await().atMost(10, TimeUnit.SECONDS)
                    .pollDelay(100, TimeUnit.MILLISECONDS)
                    .ignoreException(UnirestException.class)
                    .until(() -> restBinding.getBoundedContext().isRunning());
        }

        return restBinding;
    }

    @Deprecated
    public JMSBinding getMessageBinding()
    {
        if (messageBinding == null)
        {
            messageBinding = new JMSBinding(application, getProperties());
        }

        return messageBinding;
    }

    public <T> T getBinding(Class<T> bindingClazz)
    {
        bindingMap.putIfAbsent(bindingClazz, createInstance(bindingClazz, application, properties));
        return bindingClazz.cast(bindingMap.get(bindingClazz));
    }

    public Properties getProperties() {
        return properties;
    }

    public void shutDown()
    {
        if (messageBinding != null)
        {
            messageBinding.close();
        }
        bindingMap.clear();
        Unirest.shutDown();
        jexxaMain.stop();
    }

    private static <T> T createInstance(Class<T> clazz, Class<?> targetClass, Properties props) {
        try {
            // Suche den Konstruktor (Class, Properties)
            Constructor<T> constructor = clazz.getConstructor(Class.class, Properties.class);

            // Erzeuge Instanz
            return constructor.newInstance(targetClass, props);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz.getName() +
                    " does not provide a constructor of (Class, Properties).", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Given properties can not be used to create " + clazz.getName(), e);
        }
    }
}
