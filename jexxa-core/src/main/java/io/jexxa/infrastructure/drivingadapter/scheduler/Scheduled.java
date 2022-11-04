package io.jexxa.infrastructure.drivingadapter.scheduler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled
{
    /**
     * Execute the annotated method with a fixed period between invocations.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit()}.
     * @return the fixed period
     */
    int fixedRate();
    TimeUnit timeUnit() default MILLISECONDS;

    /**
     *  Number of units of time to delay before the first execution of a
	 * {@link #fixedRate} task
     * <p>The time unit is milliseconds by default but can be overridden via {@link #timeUnit()}
     */
    int initialDelay() default 0;
}
