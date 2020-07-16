package io.jexxa.tutorials.timeservice.infrastructure.drivingadapter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public class PublishTimeListener implements MessageListener
{
    private final TimeService timeService;
    private static final String TIME_TOPIC = "TimeService";

    public PublishTimeListener(TimeService timeService)
    {
        this.timeService = timeService;
    }

    @Override
    @JMSConfiguration(destination = TIME_TOPIC, messagingType = JMSConfiguration.MessagingType.TOPIC)
    public void onMessage(Message message)
    {
        var textMessage = (TextMessage)message;

        try {
            if ( textMessage.getText().equals("publishTime") )
            {
                timeService.publishTime();
            }
            else
            {
                JexxaLogger.getLogger(PublishTimeListener.class).warn("Unknown Message: {}", textMessage.getText());
            }

        }
        catch (JMSException jmsException)
        {
            jmsException.printStackTrace();
        }

    }
}
