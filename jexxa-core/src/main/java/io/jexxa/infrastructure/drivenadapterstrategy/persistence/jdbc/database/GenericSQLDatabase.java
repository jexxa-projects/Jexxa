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

    private SQLDataType getMaxVarChar() {
        var jdbcDriver = properties.getProperty(JDBCConnection.JDBC_URL);

        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return maxVarChar(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return maxVarChar(Integer.MAX_VALUE);
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return maxVarChar(65535);
        }

        return maxVarChar(255);
    }

    @Override
    public SQLDataType matchPrimaryKey(SQLDataType sqlDataType) {
        if (sqlDataType.equals(SQLDataType.TEXT) || sqlDataType.equals(SQLDataType.VARCHAR) || sqlDataType.equals(SQLDataType.JSONB))
        {
            return getMaxVarChar();
        }

        return sqlDataType;
    }

    @Override
    public SQLDataType matchDataType(SQLDataType sqlDataType) {
        if (sqlDataType.equals(SQLDataType.JSONB))
        {
            return SQLDataType.TEXT;
        }

        return sqlDataType;
    }

    private static SQLDataType maxVarChar(int maxSize) { return new SQLDataType("VARCHAR("+maxSize +") ");}

}
