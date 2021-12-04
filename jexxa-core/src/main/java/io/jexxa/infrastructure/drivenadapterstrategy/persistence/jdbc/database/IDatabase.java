package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

public interface IDatabase
{
    SQLDataType matchPrimaryKey(SQLDataType sqlDataType);

    SQLDataType matchDataType(SQLDataType sqlDataType);
}
