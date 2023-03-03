package io.jexxa.common.wrapper.jdbc.builder;

public class SQLSyntax
{
    static final String SELECT = "SELECT ";
    static final String UPDATE = "UPDATE ";
    static final String DELETE = "DELETE ";
    static final String INSERT_INTO = "INSERT INTO ";
    static final String SELECT_COUNT = "SELECT COUNT";

    static final String DROP_TABLE = "DROP TABLE ";
    static final String IF_EXISTS = "IF EXISTS ";

    static final String CREATE_TABLE = "CREATE TABLE ";
    static final String ALTER_TABLE = "ALTER TABLE ";
    static final String ALTER_COLUMN = "ALTER COLUMN ";
    static final String IF_NOT_EXISTS = "IF NOT EXISTS ";

    static final String SET = "SET ";
    static final String FROM = "FROM ";
    static final String WHERE = "WHERE ";
    static final String AND = "AND ";
    static final String OR = "OR ";
    static final String TYPE = "TYPE ";

    static final String ORDER_BY = "ORDER BY ";
    static final String LIMIT = "LIMIT ";

    static final String ARGUMENT_PLACEHOLDER = "? ";
    static final String COMMA = ", ";
    static final String BLANK = " ";

    public enum SQLOperation
    {
        GREATER_THAN("> "),
        GREATER_THAN_OR_EQUAL(">= "),
        LESS_THAN("< "),
        LESS_THAN_OR_EQUAL("<= "),
        EQUAL("= "),
        NOT_EQUAL("<> "),
        LIKE("LIKE "),
        NOT_LIKE("NOT LIKE "),
        IS_NULL("IS NULL "),
        IS_NOT_NULL("IS NOT NULL ");

        private final String string;

        // constructor to set the string
        SQLOperation(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
            return string;
        }
    }

}
