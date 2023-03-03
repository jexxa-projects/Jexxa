package io.jexxa.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target({METHOD, TYPE})
@Retention(CLASS)
@Documented
public @interface CheckReturnValue
{
   //Annotation to validate return value
}