package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

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

    @SuppressWarnings({"java:S100", "java:S1845"}) // Explicit naming for fluent API style
    @Deprecated(forRemoval = true)
    public static SQLDataType VARCHAR(int maxSize) { return new SQLDataType("VARCHAR("+maxSize +") ");}

    public SQLDataType(String name){string = name;}

    @Override
    public String toString()
    {
        return string;
    }

}
