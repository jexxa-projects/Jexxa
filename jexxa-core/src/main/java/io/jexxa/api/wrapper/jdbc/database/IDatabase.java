package io.jexxa.api.wrapper.jdbc.database;

import io.jexxa.api.wrapper.jdbc.JDBCConnection;
import io.jexxa.api.wrapper.jdbc.builder.SQLDataType;

/**
 * IDatabase provides a uniform interface to database operations that are vendor specific.
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

    /** Alter the type of column
     * 
     * @param jdbcConnection connection to execute the command 
     * @param tableName type of the class that is affected 
     * @param columnName name of the column 
     * @param sqlDataType new data type which must be available for this database. To avoid conflicts call {@link #matchingPrimaryKey(SQLDataType)} or {@link #matchingValue(SQLDataType)}
     */
    void alterColumnType(JDBCConnection jdbcConnection, Class<?> tableName, String columnName, SQLDataType sqlDataType);

    void renameColumn(JDBCConnection jdbcConnection, String tableName, String oldColumnName, String newColumnName);

    boolean columnExist(JDBCConnection jdbcConnection, String tableName, String columnName);
}
