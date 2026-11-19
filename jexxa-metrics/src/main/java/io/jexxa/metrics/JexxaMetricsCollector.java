package io.jexxa.metrics;

import io.jexxa.adapter.prometheus.MicrometerMetricsCollector;

import io.jexxa.core.JexxaMain;
import io.micrometer.core.instrument.MeterRegistry;

public class JexxaMetricsCollector extends MicrometerMetricsCollector {
    private static final String JLEGMED_FLOWGRAPH_MESSAGES_TOTAL = "flowgraph.messages.total";
    private static final String FLOW_GRAPH_TAG = "flow_graph";
    private static final String RESULT_TAG = "result";
    private static final String ERROR_TYPE_TAG = "error_type";

    private final JexxaMain jexxaMain;

    JexxaMetricsCollector(JexxaMain jexxaMain) {
        super(jexxaMain.getProperties());
        this.jexxaMain = jexxaMain;
        registerMetrics(this::initMetrics);
    }



    private void initMetrics(MeterRegistry registry) {
    /*    var boundedContext = jexxaMain.getBoundedContext();
        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().forwardedMessages().doubleValue())
                .description("Total processed messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "success")
                .tag(ERROR_TYPE_TAG, "none")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().handledProcessingErrors().doubleValue())
                .description("Total handled error messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "error")
                .tag(ERROR_TYPE_TAG, "handled")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().unhandledProcessingErrors().doubleValue())
                .description("Total unhandled error messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "error")
                .tag(ERROR_TYPE_TAG, "unhandled")
                .register(registry);
*/
    }

}
