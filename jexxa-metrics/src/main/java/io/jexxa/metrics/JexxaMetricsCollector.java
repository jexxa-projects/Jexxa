package io.jexxa.metrics;

import io.jexxa.adapter.prometheus.MicrometerMetricsCollector;

import io.jexxa.core.JexxaMain;
import io.jexxa.core.VersionInfo;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class JexxaMetricsCollector extends MicrometerMetricsCollector {

    private final JexxaMain jexxaMain;

    JexxaMetricsCollector(JexxaMain jexxaMain) {
        super(jexxaMain.getProperties());
        this.jexxaMain = jexxaMain;
        register(this::initMetrics);
    }



    private void initMetrics(MeterRegistry registry) {
        var boundedContext = jexxaMain.getBoundedContext();

        registerVersionInfo(registry, "jexxa.build", boundedContext.jexxaVersion());
        registerVersionInfo(registry, "jexxa.context.build", boundedContext.contextVersion());
        registerAll(registry, () -> jexxaMain.getBoundedContext().diagnostics());
    }

    private void registerVersionInfo(MeterRegistry registry, String metricPrefix, VersionInfo info) {
        if (info == null) {
            return;
        }

        // 1. Info-Metrik mit allen String-Feldern als Labels
        Gauge.builder(metricPrefix + ".info", () -> 1.0)
                .description("Build and version information for " + info.projectName() )
                .tag("version", nullSafe(info.version()))
                .tag("repository", nullSafe(info.repository()))
                .tag("project_name", nullSafe(info.projectName()))
                .register(registry);

        // 2. Timestamp als numerische Gauge (nur registrieren, wenn gültiger ISO-Timestamp vorhanden)
        parseEpochSeconds(info.buildTimestamp()).ifPresent(epochSeconds ->
                Gauge.builder(metricPrefix + ".timestamp.seconds", () -> epochSeconds)
                        .description("Build timestamp in unix epoch seconds")
                        .tag("project_name", nullSafe(info.projectName()))
                        .register(registry)
        );
    }

    private Optional<Long> parseEpochSeconds(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(Instant.parse(timestamp).getEpochSecond());
        } catch (DateTimeParseException e) {
            return java.util.Optional.empty();
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

}
