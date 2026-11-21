package io.jexxa.metrics;


import io.jexxa.core.JexxaMain;

public class MetricsCollector {
    private MetricsCollector() {
        /* This utility class should not be instantiated */
    }

    public static JexxaMetricsCollector micrometerCollector(JexxaMain jexxaMain) {
        return new JexxaMetricsCollector(jexxaMain);
    }
}
