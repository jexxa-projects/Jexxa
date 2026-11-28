package io.jexxa.metrics;

import io.jexxa.core.JexxaMain;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.adapter.prometheus.MicrometerProperties.prometheusEndpoint;
import static io.jexxa.adapter.prometheus.MicrometerProperties.prometheusPort;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JexxaMetricsCollectorTest {
    @Test
    void initFlowGraphMetrics()
    {
        //Arrange
        var jexxa = new JexxaMain(JexxaMetricsCollectorTest.class)
                .registerService(MetricsCollector::micrometerCollector);

        var jexxaProperties = jexxa.getProperties();
        jexxaProperties.setProperty(prometheusPort(), "7070");
        jexxaProperties.setProperty(prometheusEndpoint(), "/metrics");


        var url = "http://localhost:"
                + jexxaProperties.getProperty(prometheusPort())
                + jexxaProperties.getProperty(prometheusEndpoint());

        jexxa.start();

        //Act
        String rawMetrics = Unirest.get(url).asString().getBody();

        //Assert
        assertFalse(rawMetrics.isEmpty());
    }
    @AfterEach
    void afterEach()
    {
        Unirest.shutDown();
    }

}