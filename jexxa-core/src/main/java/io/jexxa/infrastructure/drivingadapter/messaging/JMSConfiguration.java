package io.jexxa.infrastructure.drivingadapter.messaging;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMSConfiguration
{
    enum MessagingType {QUEUE, TOPIC}

    String destination() default "";
    String selector() default "";
    MessagingType messagingType() default MessagingType.TOPIC;
}
