package io.jexxa.jexxatest;

import io.jexxa.core.JexxaMain;
import io.jexxa.jexxatest.integrationtest.messaging.MessageBinding;
import io.jexxa.jexxatest.integrationtest.rest.RESTBinding;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jexxatest.JexxaTest.loadJexxaTestProperties;
import static org.awaitility.Awaitility.await;

public class JexxaIntegrationTest
{
    private final Properties properties;
    private final Class<?> application;
    private RESTBinding restBinding;
    private MessageBinding messageBinding;

    public JexxaIntegrationTest(Class<?> application)
    {
        this.application = application;
        var jexxaMain = new JexxaMain(application);
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

    public MessageBinding getMessageBinding()
    {
        if (messageBinding == null)
        {
            messageBinding = new MessageBinding(application, getProperties());
        }

        return messageBinding;
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
        Unirest.shutDown();
    }
}
