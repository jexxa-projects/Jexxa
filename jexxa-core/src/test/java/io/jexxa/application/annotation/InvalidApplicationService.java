package io.jexxa.application.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Represents an ApplicationService which is invalid for testing purpose
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface InvalidApplicationService
{

}
