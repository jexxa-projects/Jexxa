package io.ddd.jexxa.infrastructure.drivingadapter.messaging;

import java.util.Properties;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.ddd.jexxa.utils.JexxaLogger;
import org.junit.Test;

public class JMSAdapterTest
{
    @Test
    public void startJMSAdapter() throws InterruptedException
    {
        //Arrange
        var messageListener = new MyListener();
        var properties = new Properties();
        properties.put(JMSAdapter.JNDI_FACTORY_KEY, JMSAdapter.DEFAULT_JNDI_FACTORY);
        properties.put(JMSAdapter.JNDI_PROVIDER_URL_KEY, JMSAdapter.DEFAULT_JNDI_PROVIDER_URL);
        properties.put(JMSAdapter.JNDI_USER_KEY, JMSAdapter.DEFAULT_JNDI_USER);
        properties.put(JMSAdapter.JNDI_PASSWORD_KEY, JMSAdapter.DEFAULT_JNDI_PASSWORD);
        var objectUnderTest = new JMSAdapter(properties);
        objectUnderTest.register(messageListener);

        objectUnderTest.start();

        System.out.println("Start Listening...");
        Thread.sleep(10000);
        objectUnderTest.close();
    }


    static public class MyListener implements MessageListener
    {

        @Override
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(MyListener.class).info(((TextMessage) message).getText());
            }
            catch ( Exception e) {
                JexxaLogger.getLogger(MyListener.class).error(e.getMessage());
            }
        }
    }

}
