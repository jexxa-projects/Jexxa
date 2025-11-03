package io.jexxa.jexxatest;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.core.JexxaMain;
import kong.unirest.Unirest;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.jexxa.jexxatest.JexxaTest.loadJexxaTestProperties;

public class JexxaIntegrationTest
{
    private final Properties properties;
    private final Class<?> application;
    private final JexxaMain jexxaMain;
    private final Map<Class<?>, AutoCloseable> bindingMap = new HashMap<>();

    public JexxaIntegrationTest(Class<?> application)
    {
        this.application = application;
        jexxaMain = new JexxaMain(application);
        jexxaMain.addProperties( loadJexxaTestProperties() );
        this.properties = jexxaMain.getProperties();
    }

    public <T  extends AutoCloseable> T getBinding(Class<T> bindingClazz)
    {
        bindingMap.putIfAbsent(bindingClazz, createInstance(bindingClazz, application, properties));
        return bindingClazz.cast(bindingMap.get(bindingClazz));
    }

    public Properties getProperties() {
        return properties;
    }

    public void shutDown()
    {
        bindingMap.values().forEach(this::close);
        bindingMap.clear();
        Unirest.shutDown();
        jexxaMain.stop();
    }

    private void close(AutoCloseable autoCloseable)
    {
        try{
            autoCloseable.close();
        } catch (Exception e){
            SLF4jLogger.getLogger(JexxaIntegrationTest.class).error("Could not close Binding {}", e.getMessage());
        }
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
