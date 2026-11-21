package io.jexxa.metrics;

import io.jexxa.core.JexxaMain;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;


import static io.jexxa.adapter.prometheus.MicrometerProperties.prometheusEndpoint;
import static io.jexxa.adapter.prometheus.MicrometerProperties.prometheusPort;
import static org.awaitility.Awaitility.await;

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
        //ACT / ASSERT
        await().atMost(Duration.ofSeconds(5)).until(() -> getProcessedMessages(url) > 0);
    }
    @AfterEach
    void afterEach()
    {
        Unirest.shutDown();
    }

    private int getProcessedMessages(String url) {
        String rawMetrics = Unirest.get(url).asString().getBody();

        if (rawMetrics == null || rawMetrics.isBlank()) {
            return 0;
        }

        System.out.println(rawMetrics);

        return 1;

        // Zeilenweise verarbeiten - absolut linear, kein Backtracking möglich
/*        return rawMetrics.lines()
                // 1. Nur die relevante Metrik-Zeile heraussuchen
                .filter(line -> line.startsWith("flowgraph_messages_total") && line.contains("result=\"success\""))
                .map(line -> {
                    try {
                        int closingBracketIndex = line.lastIndexOf('}');
                        if (closingBracketIndex == -1) return 0;

                        // Wert nach der Klammer ausschneiden und trimmen (z.B. "6820.0")
                        String valueStr = line.substring(closingBracketIndex + 1).trim();

                        // Das ".0" von Prometheus abschneiden, um es sicher in Integer zu wandeln
                        int dotIndex = valueStr.indexOf('.');
                        if (dotIndex != -1) {
                            valueStr = valueStr.substring(0, dotIndex);
                        }

                        return parseInt(valueStr);
                    } catch (Exception _) {
                        return 0; // Fehlerhafte Zeile ignorieren
                    }
                })
                .findFirst()
                .orElse(0);*/
    }

}