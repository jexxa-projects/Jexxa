package io.jexxa.adapterapi.drivingadapter;

public interface HealthCheck
{
    boolean healthy();

    Diagnostics getDiagnostics();

    class Diagnostics
    {
        private final boolean isHealthy;
        private final String statusMessage;
        private final Class<?> healthCheck;
        private final Class<?> observedObject;

        public Diagnostics(Class<?> healthCheck, Class<?> observedObject, boolean isHealthy, String statusMessage)
        {
            this.healthCheck = healthCheck;
            this.isHealthy = isHealthy;
            this.statusMessage = statusMessage;
            this.observedObject = observedObject;
        }

        public Class<?> getHealthCheck() {
            return healthCheck;
        }

        public Class<?> getObservedObject() {
            return observedObject;
        }

        public boolean isHealthy() {
            return isHealthy;
        }

        public String getStatusMessage() {
            return statusMessage;
        }
    }
}
