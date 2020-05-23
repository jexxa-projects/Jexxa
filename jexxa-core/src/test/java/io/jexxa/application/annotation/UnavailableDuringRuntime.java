package io.jexxa.application.annotation;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(SOURCE)
@Documented
/*
 * Annotation that is not available during runtime. This is for testing purpose only
 */
public @interface UnavailableDuringRuntime
{

}
