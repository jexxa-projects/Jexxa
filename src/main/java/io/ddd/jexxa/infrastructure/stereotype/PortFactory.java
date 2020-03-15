package io.ddd.jexxa.infrastructure.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Represents a Factory that creates a port in terms of a hexagonal architecture.
 *
 * Within DDD typical ports are an ApplicationService or objects providing a {@link io.ddd.stereotype.applicationcore.DomainEventHandler}
 * (which are typically a {@link io.ddd.stereotype.applicationcore.DomainService} or {@link io.ddd.stereotype.applicationcore.DomainProcess}.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface PortFactory
{
}
