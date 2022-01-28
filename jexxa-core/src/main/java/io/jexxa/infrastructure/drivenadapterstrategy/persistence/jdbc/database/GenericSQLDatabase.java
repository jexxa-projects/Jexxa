package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCCommand;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc.JDBCKeyValueRepository;
import io.jexxa.utils.properties.JexxaJDBCProperties;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class GenericSQLDatabase implements IDatabase
{
    private final Properties properties;

    GenericSQLDatabase(Properties properties)
    {
        this.properties = properties;
    }

    private SQLDataType getMaxVarChar() {
        var jdbcDriver = properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL);

        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return maxVarChar(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return SQLDataType.VARCHAR;
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return maxVarChar(65535);
        }

        return maxVarChar(255);
    }

    @Override
    public SQLDataType matchingPrimaryKey(SQLDataType requestedDataType)
    {
        if (requestedDataType.equals(SQLDataType.TEXT) || requestedDataType.equals(SQLDataType.VARCHAR) || requestedDataType.equals(SQLDataType.JSONB))
        {
            return getMaxVarChar();
        }

        return requestedDataType;
    }

    @Override
    public SQLDataType matchingValue(SQLDataType requestedDataType)
    {
        if (requestedDataType.equals(SQLDataType.JSONB))
        {
            return SQLDataType.TEXT;
        }

        return requestedDataType;
    }

    @Override
    public void alterColumnType(JDBCConnection jdbcConnection, Class<?> tableName, String columnName, SQLDataType sqlDataType)
    {
        jdbcConnection.createTableCommand(JDBCKeyValueRepository.KeyValueSchema.class)
                .alterTable(tableName)
                .alterColumn(columnName, sqlDataType)
                .create()
                .asIgnore();
    }


    @Override
    public void renameColumn(JDBCConnection jdbcConnection, String tableName, String oldColumnName, String newColumnName)
    {
        var renameColumnCommand =  "ALTER TABLE "
                + tableName.toLowerCase()
                + " RENAME COLUMN "
                + oldColumnName.toLowerCase()
                + " TO "
                +  newColumnName.toLowerCase();

        var renameCommand = new JDBCCommand(
                ()->jdbcConnection,
                renameColumnCommand,
                List.of()
        );
        renameCommand.asIgnore();
    }


    @Override
    public boolean columnExist(JDBCConnection jdbcConnection, String tableName, String columnName)
    {
        var columnExist = "SELECT column_name FROM information_schema.columns WHERE table_name= ?  and column_name= ? ";
        var query = new JDBCQuery(() -> jdbcConnection,
                columnExist,
                List.of(tableName.toLowerCase(), columnName.toLowerCase())
        );

        return query
                .asString()
                .flatMap(Optional::stream)
                .findAny().isPresent();
    }

    private static SQLDataType maxVarChar(int maxSize)
    {
        return new SQLDataType("VARCHAR("+maxSize +") ");
    }

}
