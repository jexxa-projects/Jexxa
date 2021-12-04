package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

public interface IDatabase
{
    //void createTable(); //Schema

    SQLDataType getKeyDataType();

    SQLDataType getValueDataType();

    String getBindParameter();

    JDBCObject getJDBCObject(Object value);

    JDBCObject getJDBCObject(Object value,SQLDataType sqlDataType);

}
