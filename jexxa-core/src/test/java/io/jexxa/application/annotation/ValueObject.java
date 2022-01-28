package io.jexxa.application.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Represents a ValueObject.
 */
@Target(TYPE)
@Documented
public @interface ValueObject
{

}
