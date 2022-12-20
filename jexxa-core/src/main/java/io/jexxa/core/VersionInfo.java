package io.jexxa.core;

@SuppressWarnings("unused")
public record VersionInfo(String version, String repository, String projectName, String buildTimestamp)
{
    public static VersionInfoBuilder of() {
        return new VersionInfoBuilder();
    }

    public static class VersionInfoBuilder {
        private String version;
        private String repository;
        private String projectName;
        private String buildTimestamp;

        public VersionInfoBuilder version(String version) {
            this.version = version;
            return this;
        }

        public VersionInfoBuilder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public VersionInfoBuilder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public VersionInfoBuilder buildTimestamp(String buildTimestamp) {
            this.buildTimestamp = buildTimestamp;
            return this;
        }

        public VersionInfo create() {
            return new VersionInfo(version, repository, projectName, buildTimestamp);
        }
    }
}
