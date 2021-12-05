package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

public class PostgresDatabase implements IDatabase
{
    @Override
    public SQLDataType matchPrimaryKey(SQLDataType sqlDataType) {
        return sqlDataType;
    }

    @Override
    public SQLDataType matchDataType(SQLDataType sqlDataType) {
        return sqlDataType;
    }

    @Override
    public SQLDataType alterDataTypeTo(SQLDataType sqlDataType)
    {
        return sqlDataType;
    }

    @Override
    public String alterColumnUsingStatement(Enum<?> columnName, SQLDataType sqlDataType)
    {
        return " USING " + columnName.name() + "::" + sqlDataType.toString();
    }

    @Override
    public SQLDataType alterPrimaryKeyTo(SQLDataType sqlDataType)
    {
        return sqlDataType;
    }

}
