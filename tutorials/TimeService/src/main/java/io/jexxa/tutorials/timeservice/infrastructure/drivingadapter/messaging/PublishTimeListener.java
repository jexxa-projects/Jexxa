package io.jexxa.tutorials.timeservice.infrastructure.drivingadapter.messaging;

import static io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration.MessagingType.TOPIC;

import java.time.LocalTime;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public class PublishTimeListener implements MessageListener
{
    private final TimeService timeService;
    private static final String TIME_TOPIC = "TimeService";

    //To implement a so called PortAdapter we need a public constructor which expects a single argument that must be a InboundPort.
    public PublishTimeListener(TimeService timeService)
    {
        this.timeService = timeService;
    }

    @Override
    // The JMS specific configuration is defined via annotation.
    @JMSConfiguration(destination = TIME_TOPIC,  messagingType = TOPIC)
    public void onMessage(Message message)
    {
        try
        {
            // The JMSSender sends all messages as TextMessage in Json encoding
            var textMessage = (TextMessage)message;

            // Deserialize the message which is of type 'LocalTime'
            var time = new Gson().fromJson(textMessage.getText(), LocalTime.class);

            // Forward this information to corresponding application service.
            timeService.displayPublishedTime(time);
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(PublishTimeListener.class).error(exception.getMessage());
        }
    }
}
