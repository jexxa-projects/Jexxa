package io.jexxa.core;

class JexxaVersion
{
    public static final String VERSION = "${project.version}";
    public static final String REPOSITORY = "${project.scm.developerConnection}";
    public static final String PROJECT_NAME= "${project.name}";
    public static final String BUILD_TIMESTAMP= "${build.timestamp}";

    private JexxaVersion()
    {
        //Private constructor
    }
}