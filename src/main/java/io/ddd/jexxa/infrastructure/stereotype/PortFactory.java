package io.ddd.jexxa.infrastructure.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Represents a Factory that creates a port in terms of a hexagonal architecture.
 *
 * Within DDD typical ports are an ApplicationService or objects providing a DomainEventHandler
 * (which are typically a DomainService or DomainProcess.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface PortFactory
{
}
