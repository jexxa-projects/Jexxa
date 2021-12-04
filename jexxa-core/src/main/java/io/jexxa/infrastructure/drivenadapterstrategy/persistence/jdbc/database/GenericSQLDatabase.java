package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

import java.util.Properties;

public class GenericSQLDatabase implements IDatabase
{
    private final Properties properties;

    GenericSQLDatabase(Properties properties)
    {
        this.properties = properties;
    }

    @Override
    public SQLDataType getKeyDataType() {
        var jdbcDriver = properties.getProperty(JDBCConnection.JDBC_URL);

        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return SQLDataType.VARCHAR(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return SQLDataType.VARCHAR; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return SQLDataType.VARCHAR(Integer.MAX_VALUE);
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return SQLDataType.VARCHAR(65535);
        }

        return SQLDataType.VARCHAR(255);
    }

    @Override
    public SQLDataType getValueDataType() {
        return SQLDataType.TEXT;
    }

    @Override
    public String getBindParameter() {
        return "? ";
    }
}
