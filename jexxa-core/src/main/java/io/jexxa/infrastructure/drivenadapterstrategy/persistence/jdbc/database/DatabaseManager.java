package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.utils.properties.JexxaJDBCProperties;

import java.util.Locale;
import java.util.Properties;

final public class DatabaseManager
{
    public static IDatabase getDatabase(Properties properties)
    {
        var jdbcDriver = properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL);

        if ( jdbcDriver.toLowerCase(Locale.ENGLISH).contains("postgres") )
        {
            return new PostgresDatabase(properties);
        }

        return new GenericSQLDatabase(properties);
    }

    private DatabaseManager()
    {
        // private constructor
    }

}
