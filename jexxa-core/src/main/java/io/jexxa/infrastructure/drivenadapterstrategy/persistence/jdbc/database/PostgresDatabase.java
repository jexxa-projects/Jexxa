package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.JSONB;

public class PostgresDatabase implements IDatabase
{
    @Override
    public SQLDataType getKeyDataType() {
        return JSONB;
    }

    @Override
    public SQLDataType getValueDataType() {
        return JSONB;
    }

    @Override
    public String getBindParameter() {
        return "(?::"+ JSONB + ")";
    }
}
