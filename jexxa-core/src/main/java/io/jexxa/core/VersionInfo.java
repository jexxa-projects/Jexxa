package io.jexxa.core;

public class VersionInfo {
    private final String version;
    private final String repository;
    private final String projectName;
    private final String buildTimestamp;

    private VersionInfo(String version, String repository, String projectName, String buildTimestamp)
    {
        this.version = version;
        this.repository = repository;
        this.projectName = projectName;
        this.buildTimestamp = buildTimestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getRepository() {
        return repository;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    public String toString()
    {
        return getProjectName() + " "
                + getVersion() + "; built: "
                + getBuildTimestamp() + "; vcs: "
                + getRepository() + ";";
    }

    public static VersionInfoBuilder of()
    {
        return new VersionInfoBuilder();
    }

    public static class VersionInfoBuilder {
        private String version;
        private String repository;

        private String projectName;
        private String buildTimestamp;

        public VersionInfoBuilder version(String version)
        {
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

        public VersionInfo create()
        {
            return new VersionInfo(version, repository, projectName, buildTimestamp);
        }
    }
}
