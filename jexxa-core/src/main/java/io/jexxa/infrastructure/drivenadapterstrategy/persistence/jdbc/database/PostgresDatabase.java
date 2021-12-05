package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc.JDBCKeyValueRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class PostgresDatabase extends GenericSQLDatabase
{
    PostgresDatabase(Properties properties) {
        super(properties);
    }

    @Override
    public SQLDataType matchingPrimaryKey(SQLDataType requestedDataType) {
        return requestedDataType;
    }

    @Override
    public SQLDataType matchingValue(SQLDataType requestedDataType) {
        return requestedDataType;
    }


    @Override
    public void alterColumnType(JDBCConnection jdbcConnection, Class<?> tableName, String columnName, SQLDataType sqlDataType) {
        var keyRow = jdbcConnection.createTableCommand(JDBCKeyValueRepository.KeyValueSchema.class)
                .alterTable(tableName)
                .alterColumn(columnName, sqlDataType, " USING " + columnName + "::" + sqlDataType)
                .create();

        keyRow.asIgnore();
    }


    @Override
    public boolean columnExist(JDBCConnection jdbcConnection, String tableName, String columnName) {
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


}
