package io.jexxa.api.wrapper.jdbc.database;

import io.jexxa.api.wrapper.jdbc.JDBCCommand;
import io.jexxa.api.wrapper.jdbc.JDBCConnection;
import io.jexxa.api.wrapper.jdbc.JDBCQuery;
import io.jexxa.api.wrapper.jdbc.builder.SQLDataType;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GenericSQLDatabase implements IDatabase
{
    private final String connectionURL;

    GenericSQLDatabase(String connectionURL)
    {
        this.connectionURL = connectionURL;
    }

    private SQLDataType getMaxVarChar() {
        if ( connectionURL.toLowerCase(Locale.ENGLISH).contains("oracle") )
        {
            return maxVarChar(4000);
        }

        if ( connectionURL.toLowerCase(Locale.ENGLISH).contains("h2") )
        {
            return SQLDataType.VARCHAR;
        }

        if ( connectionURL.toLowerCase(Locale.ENGLISH).contains("mysql") )
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
        jdbcConnection.createTableCommand()
                .alterTable(tableName)
                .alterColumn(columnName, sqlDataType)
                .create()
                .asIgnore();
    }


    @Override
    public void renameColumn(JDBCConnection jdbcConnection, String tableName, String oldColumnName, String newColumnName)
    {
        var renameColumnCommand =  "ALTER TABLE "
                + tableName.toLowerCase(Locale.ENGLISH)
                + " RENAME COLUMN "
                + oldColumnName.toLowerCase(Locale.ENGLISH)
                + " TO "
                +  newColumnName.toLowerCase(Locale.ENGLISH);

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
                List.of(tableName.toLowerCase(Locale.ENGLISH), columnName.toLowerCase(Locale.ENGLISH))
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
