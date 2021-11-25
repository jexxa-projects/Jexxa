package io.jexxa.core;

class JexxaVersion
{
    public static final String VERSION = "${project.version}";
    public static final String REPOSITORY = "${project.scm.developerConnection}";
    public static final String PROJECT_NAME= "${project.name}";
    public static final String BUILD_TIMESTAMP= "${build.timestamp}";

    private final String version;

    private final String repository;
    private final String projectName;
    private final String buildTimestamp;

    private JexxaVersion()
    {
        this.version = VERSION;
        this.repository = REPOSITORY;
        this.projectName = PROJECT_NAME;
        this.buildTimestamp = BUILD_TIMESTAMP;
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


    public static JexxaVersion getJexxaVersion()
    {
        return new JexxaVersion();
    }
}