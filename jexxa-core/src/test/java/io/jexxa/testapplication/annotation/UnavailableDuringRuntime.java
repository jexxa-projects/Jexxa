package io.jexxa.testapplication.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(TYPE)
@Retention(SOURCE)
@Documented
/*
 * Annotation that is not available during runtime. This is for testing purpose only
 */
public @interface UnavailableDuringRuntime
{

}
