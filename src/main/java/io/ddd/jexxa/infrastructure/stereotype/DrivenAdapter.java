package io.ddd.jexxa.infrastructure.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A {@link DrivenAdapter} is an implementation of an outbound port of an application core such as {@link io.ddd.stereotype.applicationcore.Repository} or an {@link io.ddd.stereotype.applicationcore.InfrastructureService}.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DrivenAdapter
{
}
