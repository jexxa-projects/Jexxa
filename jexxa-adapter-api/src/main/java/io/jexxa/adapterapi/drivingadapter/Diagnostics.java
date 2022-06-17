package io.jexxa.adapterapi.drivingadapter;

import java.time.Instant;

public record Diagnostics(String nameOfHealthCheck,
                          String nameOfObservedObject,
                          boolean isHealthy,
                          String statusMessage,
                          Instant timestamp)
{

}