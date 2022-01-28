package io.jexxa.infrastructure.drivingadapter.messaging;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMSConfiguration
{
    enum MessagingType {QUEUE, TOPIC}

    String destination() ;
    String selector() default "";
    MessagingType messagingType() ;
}
