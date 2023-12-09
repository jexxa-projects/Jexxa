package io.jexxa.properties;

public final class JexxaCoreProperties
{
    /** Define an additional import file that is loaded. The Default value is empty.  */
    public static final String JEXXA_CONFIG_IMPORT =  "io.jexxa.config.import";

    /** Define the name of the bounded context. The Default value is the name of the main class of an application   */
    public static final String JEXXA_CONTEXT_NAME =  "io.jexxa.context.name";

    /** Defines the version number of the context. This is typically set via maven */
    public static final String JEXXA_CONTEXT_VERSION = "io.jexxa.context.version";

    /** Defines the repository of the context. This is typically set via maven */
    public static final String JEXXA_CONTEXT_REPOSITORY = "io.jexxa.context.repository";

    /** Defines the build timestamp of the context. This is typically set via maven */
    public static final String JEXXA_CONTEXT_BUILD_TIMESTAMP = "io.jexxa.context.build.timestamp";

    /** Configures the global system property user.timezone to define the timezone used by the application */
    public static final String JEXXA_USER_TIMEZONE = "io.jexxa.user.timezone";

    /** Defines the default properties file which is /jexxa-application.properties */
    public static final String JEXXA_APPLICATION_PROPERTIES = "/jexxa-application.properties";

    private JexxaCoreProperties()
    {
        //Private constructor
    }
}
