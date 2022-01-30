package io.jexxa.core;

public interface HealthCheck
{
    boolean healthy();

    Diagnostics getDiagnostics();

    class Diagnostics
    {
        private final boolean isHealthy;
        private final String healthCheckName;
        private final String statusMessage;

        public Diagnostics(String healthCheckName, boolean isHealty, String statusMessage) {
            this.healthCheckName = healthCheckName;
            this.isHealthy = isHealty;
            this.statusMessage = statusMessage;
        }

        public String getName() {
            return healthCheckName;
        }

        public boolean isHealthy() {
            return isHealthy;
        }

        public String getStatusMessage() {
            return statusMessage;
        }
    }
}
