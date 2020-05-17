package io.jexxa.infrastructure.drivingadapter.messaging;

import javax.jms.Message;
import javax.jms.MessageListener;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class SynchronizedMessageListener implements MessageListener
{
    private final MessageListener jmsListener;

    SynchronizedMessageListener(MessageListener jmsListener )
    {
        Validate.notNull(jmsListener);
        this.jmsListener = jmsListener;
    }


    @Override
    public void onMessage(Message message)
    {
        synchronized (IDrivingAdapter.acquireLock().getSynchronizationObject())
        {
            jmsListener.onMessage(message);
        }
    }
}
