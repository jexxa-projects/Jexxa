package io.jexxa.common;

public final class JexxaJDBCProperties
{
    public static final String JEXXA_JDBC_FILE_USERNAME = "io.jexxa.jdbc.file.username";
    public static final String JEXXA_JDBC_FILE_PASSWORD = "io.jexxa.jdbc.file.password";

    public static final String JEXXA_JDBC_URL = "io.jexxa.jdbc.url";
    public static final String JEXXA_JDBC_USERNAME = "io.jexxa.jdbc.username";
    public static final String JEXXA_JDBC_PASSWORD = "io.jexxa.jdbc.password";
    public static final String JEXXA_JDBC_DRIVER = "io.jexxa.jdbc.driver";
    public static final String JEXXA_JDBC_AUTOCREATE_DATABASE = "io.jexxa.jdbc.autocreate.database";
    public static final String JEXXA_JDBC_AUTOCREATE_TABLE = "io.jexxa.jdbc.autocreate.table";

    /** Defines the jdbc transaction level. This must be one of the following values "read-uncommitted", "read-committed", "repeatable-read", "serializable"*/
    public static final String JEXXA_JDBC_TRANSACTION_ISOLATION_LEVEL = "io.jexxa.jdbc.transaction.isolation.level";

    public static final String JEXXA_REPOSITORY_STRATEGY = "io.jexxa.repository.strategy";

    public static final String JEXXA_OBJECTSTORE_STRATEGY = "io.jexxa.objectstore.strategy";

    private JexxaJDBCProperties()
    {
        //private constructor
    }

}