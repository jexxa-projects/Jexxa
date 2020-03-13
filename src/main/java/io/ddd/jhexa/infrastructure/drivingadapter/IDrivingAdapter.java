package io.ddd.Jexxa.infrastructure.drivingadapter;

/**
 * Die SRP dieses Interfaces ist die Bereitstellung einer Schnittstelle für Subsysteme, die gestartet und gestoppt werden können.
 */
public interface IDrivingAdapter
{
    /**
     * Starte Subsystem
     */
    void start();

    /**
     * Stoppe Subsystem
     */

    void stop();
}
