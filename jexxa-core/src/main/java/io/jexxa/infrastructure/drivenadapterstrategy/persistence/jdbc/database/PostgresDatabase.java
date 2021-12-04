package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc.JDBCStringQuery;

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

    @Override
    public JDBCObject getJDBCObject(Object value) {
        return new JDBCObject(value, getBindParameter());
    }

    @Override
    public JDBCObject getJDBCObject(Object value, SQLDataType sqlDataType) {
        if ( sqlDataType == JSONB )
        {
            return new JDBCObject(value, getBindParameter());
        }
        return new JDBCObject(value, "? ");
    }
}
