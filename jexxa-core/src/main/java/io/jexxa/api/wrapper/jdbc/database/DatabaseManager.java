package io.jexxa.api.wrapper.jdbc.database;

import java.util.Locale;

public final class DatabaseManager
{
    public static IDatabase getDatabase(String connectionURL)
    {
        if ( connectionURL.toLowerCase(Locale.ENGLISH).contains("postgres") )
        {
            return new PostgresDatabase(connectionURL);
        }

        return new GenericSQLDatabase(connectionURL);
    }

    private DatabaseManager()
    {
        // private constructor
    }

}
