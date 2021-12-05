package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

/**
 * IDatabase pro
 */
public interface IDatabase
{
    /**
     * Match the desired data type for primary keys to a data type suppoerted by the database.
     *
     * @param requestedDataType data type that is requested by the application
     * @return SQL data type that is supported by the database and most likely fits to the requested data type
     */
    SQLDataType matchingPrimaryKey(SQLDataType requestedDataType);

    /**
     * Match the desired data type for values to a data type suppoerted by the database
     *
     * @param requestedDataType data type that is requested by the application
     * @return SQL data type that is supported by the database and most likely fits to the requested data type
     */
    SQLDataType matchingValue(SQLDataType requestedDataType);

    void alterColumnType(JDBCConnection jdbcConnection, Class<?> tableName, String columnName, SQLDataType sqlDataType);

    void renameColumn(JDBCConnection jdbcConnection, String tableName, String oldColumnName, String newColumnName);

    boolean columnExist(JDBCConnection jdbcConnection, String tableName, String columnName);
}
