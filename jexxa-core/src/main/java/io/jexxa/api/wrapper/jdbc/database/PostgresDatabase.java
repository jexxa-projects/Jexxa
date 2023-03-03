package io.jexxa.api.wrapper.jdbc.database;

import io.jexxa.api.wrapper.jdbc.JDBCConnection;
import io.jexxa.api.wrapper.jdbc.builder.SQLDataType;
import io.jexxa.drivenadapter.strategy.persistence.repository.jdbc.JDBCKeyValueRepository;

public class PostgresDatabase extends GenericSQLDatabase
{
    PostgresDatabase(String connectionURL) {
        super(connectionURL);
    }

    @Override
    public SQLDataType matchingPrimaryKey(SQLDataType requestedDataType)
    {
        return requestedDataType;
    }

    @Override
    public SQLDataType matchingValue(SQLDataType requestedDataType)
    {
        return requestedDataType;
    }


    @Override
    public void alterColumnType(JDBCConnection jdbcConnection, Class<?> tableName, String columnName, SQLDataType sqlDataType)
    {
        var keyRow = jdbcConnection.createTableCommand(JDBCKeyValueRepository.KeyValueSchema.class)
                .alterTable(tableName)
                .alterColumn(columnName, sqlDataType, " USING " + columnName + "::" + sqlDataType)
                .create();

        keyRow.asIgnore();
    }

}
