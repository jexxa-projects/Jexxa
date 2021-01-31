package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

class SQLSyntax
{
    static final String UPDATE = "UPDATE ";
    static final String INSERT_INTO = "INSERT INTO ";
    static final String REMOVE = "REMOVE ";
    static final String FROM = "FROM ";
    static final String WHERE = "WHERE ";
    static final String DROP_TABLE = "DROP TABLE ";
    static final String IF_EXISTS = "IF EXISTS ";

    static final String ARGUMENT_PLACEHOLDER = " ? ";
    static final String BLANK = " ";

    enum SQLOperation
    {
        GREATER_THAN(">"),
        LESS_THAN("<"),
        EQUAL("=");

        private final String string;

        // constructor to set the string
        SQLOperation(String name){string = name;}

        // the toString just returns the given name
        @Override
        public String toString() {
            return string;
        }
    }


}
