package io.ddd.jexxa.infrastructure.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A {@link DrivingAdapter} drives an application core by receiving commands via a specific protocol such as REST or JMS.
 * A {@link DrivingAdapter} can use available inbound ports provided by the application core to execute commands or
 * a {{@link io.ddd.stereotype.applicationcore.DomainEvent} and 'drive' the application.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DrivingAdapter
{
}
