package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

public class SQLSyntax
{
    static final String SELECT = "SELECT ";
    static final String UPDATE = "UPDATE ";
    static final String DELETE = "DELETE ";
    static final String INSERT_INTO = "INSERT INTO ";

    static final String DROP_TABLE = "DROP TABLE ";
    static final String IF_EXISTS = "IF EXISTS ";

    static final String CREATE_TABLE = "CREATE TABLE ";
    static final String IF_NOT_EXISTS = "IF NOT EXISTS ";

    static final String SET = "SET ";
    static final String FROM = "FROM ";
    static final String WHERE = "WHERE ";
    static final String AND = "AND ";
    static final String OR = "OR ";

    static final String ARGUMENT_PLACEHOLDER = " ? ";
    static final String COMMA = " , ";
    static final String BLANK = " ";

    enum SQLOperation
    {
        GREATER_THAN("> "),
        GREATER_THAN_OR_EQUAL(">= "),
        LESS_THAN("< "),
        LESS_THAN_OR_EQUAL("<= "),
        EQUAL("= "),
        NOT_EQUAL("<> "),
        LIKE("LIKE ");

        private final String string;

        // constructor to set the string
        SQLOperation(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
            return string;
        }
    }

    public static class SQLDataType
    {
        private final String string;

        public static final SQLDataType INTEGER = new SQLDataType("INTEGER ");
        public static final SQLDataType NUMERIC = new SQLDataType("NUMERIC ");
        public static final SQLDataType FLOAT = new SQLDataType("FLOAT ");
        public static final SQLDataType DOUBLE = new SQLDataType("DOUBLE PRECISION ");
        public static final SQLDataType TIMESTAMP = new SQLDataType("TIMESTAMP ");
        public static final SQLDataType TEXT = new SQLDataType("TEXT ");
        public static final SQLDataType VARCHAR = new SQLDataType("VARCHAR ");

        @SuppressWarnings({"java:S100", "java:S1845"}) // Explicit naming for fluent API style
        public static SQLDataType VARCHAR(int maxSize) { return new SQLDataType("VARCHAR("+maxSize +") ");}

        protected SQLDataType(String name){string = name;}

        @Override
        public String toString()
        {
            return string;
        }
    }


    public enum SQLConstraint
    {
        PRIMARY_KEY("PRIMARY KEY");

        private final String string;

        // constructor to set the string
        SQLConstraint(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
            return string;
        }
    }
}
