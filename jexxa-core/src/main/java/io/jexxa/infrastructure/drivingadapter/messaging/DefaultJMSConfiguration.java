package io.jexxa.infrastructure.drivingadapter.messaging;

import java.lang.annotation.Annotation;

@SuppressWarnings({"ClassCanBeRecord", "ClassExplicitlyAnnotation"})
public class DefaultJMSConfiguration implements JMSConfiguration {

    private final String destination;
    private final MessagingType messagingType;

    public DefaultJMSConfiguration(String destination, MessagingType messagingType)
    {
        this.destination = destination;
        this.messagingType = messagingType;
    }
    @Override
    public String destination() {
        return destination;
    }

    @Override
    public String selector() {
        return "";
    }

    @Override
    public MessagingType messagingType() {
        return messagingType;
    }

    @Override
    public String sharedSubscriptionName() {
        return "";
    }

    @Override
    public DurableType durable() {
        return DurableType.NON_DURABLE;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JMSConfiguration.class;
    }
}
