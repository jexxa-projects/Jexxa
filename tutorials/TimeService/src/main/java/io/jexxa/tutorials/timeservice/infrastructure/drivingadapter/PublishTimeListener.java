package io.jexxa.tutorials.timeservice.infrastructure.drivingadapter;

import java.time.LocalTime;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;

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
        try
        {
            var time = new Gson().fromJson(textMessage.getText(), LocalTime.class);

            timeService.timePublished(time);
        }
        catch (JMSException jmsException)
        {
            jmsException.printStackTrace();
        }

    }
}
