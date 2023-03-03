package io.jexxa.common.wrapper.jdbc.builder;

public class SQLDataType
{
    private final String string;

    public static final SQLDataType INTEGER = new SQLDataType("INTEGER ");
    public static final SQLDataType NUMERIC = new SQLDataType("NUMERIC ");
    public static final SQLDataType FLOAT = new SQLDataType("FLOAT ");
    public static final SQLDataType DOUBLE = new SQLDataType("DOUBLE PRECISION ");
    public static final SQLDataType TIMESTAMP = new SQLDataType("TIMESTAMP ");
    public static final SQLDataType TEXT = new SQLDataType("TEXT ");
    public static final SQLDataType VARCHAR = new SQLDataType("VARCHAR ");
    public static final SQLDataType JSONB = new SQLDataType("JSONB ");

    public SQLDataType(String name){string = name;}

    @Override
    public String toString()
    {
        return string;
    }

}
