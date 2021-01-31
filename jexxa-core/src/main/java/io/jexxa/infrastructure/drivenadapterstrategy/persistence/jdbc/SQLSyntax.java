package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

public class SQLSyntax
{
    static final String SELECT = "SELECT ";
    static final String UPDATE = "UPDATE ";
    static final String REMOVE = "REMOVE ";
    static final String INSERT_INTO = "INSERT INTO ";

    static final String DROP_TABLE = "DROP TABLE ";
    static final String IF_EXISTS = "IF EXISTS ";

    static final String CREATE_TABLE = "CREATE TABLE ";
    static final String IF_NOT_EXISTS = "IF NOT EXISTS ";

    static final String FROM = "FROM ";
    static final String WHERE = "WHERE ";

    static final String ARGUMENT_PLACEHOLDER = " ? ";
    static final String COMMA = " , ";
    static final String BLANK = " ";

    enum SQLOperation
    {
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL("<="),
        EQUAL("="),
        NOT_EQUAL("!=");

        private final String string;

        // constructor to set the string
        SQLOperation(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
            return string;
        }
    }

    public enum SQLDataType
    {
        INTEGER("INTEGER "),
        NUMERIC("NUMERIC "),
        FLOAT("FLOAT "),
        DOUBLE("DOUBLE PRECISION "),
        VARCHAR("VARCHAR "),
        TEXT("TEXT "),
        TIMESTAMP("TIMESTAMP ");

        private final String string;

        // constructor to set the string
        SQLDataType(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
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
