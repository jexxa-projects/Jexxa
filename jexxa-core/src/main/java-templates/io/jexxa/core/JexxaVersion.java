package io.jexxa.core;

final class JexxaVersion
{
    public static final String VERSION = "${project.version}";
    public static final String REPOSITORY = "${project.scm.developerConnection}";
    public static final String PROJECT_NAME= "${project.name}";
    public static final String BUILD_TIMESTAMP= "${build.timestamp}";

    private JexxaVersion()
    {
        //private constructor
    }

    public static VersionInfo getJexxaVersion()
    {
        return VersionInfo.of()
                .version(VERSION)
                .repository(REPOSITORY)
                .buildTimestamp(BUILD_TIMESTAMP)
                .projectName(PROJECT_NAME)
                .create();
    }
}