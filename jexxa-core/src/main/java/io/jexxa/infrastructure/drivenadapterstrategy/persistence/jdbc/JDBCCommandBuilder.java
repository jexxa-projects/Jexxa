package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>>
{
    private static final String UPDATE = "UPDATE ";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String REMOVE = "REMOVE ";
    private static final String FROM = "FROM ";
    private static final String WHERE = "WHERE ";
    private static final String DROP_TABLE = "DROP TABLE ";
    private static final String IF_EXISTS = "IF EXISTS ";

    private static final String ARGUMENT_PLACEHOLDER = " ? ";
    private static final String BLANK = " ";

    private final StringBuilder sqlCommandBuilder = new StringBuilder();
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

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

    public JDBCCommandBuilder<T> update(T element)
    {
        sqlCommandBuilder
                .append(UPDATE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        sqlCommandBuilder
                .append(INSERT_INTO)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(INSERT_INTO)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> values(Object... args)
    {
        sqlCommandBuilder.append("values ( ");
        for(int i = 0;  i < args.length; ++i )
        {
            sqlCommandBuilder.append( " ? " );
            if ( i < args.length -1)
            {
                sqlCommandBuilder.append( " , " );
            }
            arguments.add(args[i]);
        }
        sqlCommandBuilder.append(")");

        return this;
    }

    public JDBCCommandBuilder<T> remove(T element)
    {
        sqlCommandBuilder
                .append(REMOVE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(T element)
    {
        sqlCommandBuilder
                .append(FROM)
                .append( element.name() )
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> where(T element)
    {
        sqlCommandBuilder
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> isEqual(Object value)
    {
        return is(SQLOperation.EQUAL, value);
    }

    public JDBCCommandBuilder<T> isLessThan(Object value)
    {
        return is(SQLOperation.LESS_THAN, value);
    }

    public JDBCCommandBuilder<T> isGreaterThan(Object value)
    {
        return is(SQLOperation.GREATER_THAN, value);
    }


    public String getCommand()
    {
        return sqlCommandBuilder.toString();
    }

    public JDBCCommandBuilder<T> is(SQLOperation operation, Object attribute)
    {
        sqlCommandBuilder
                .append(operation.toString())
                .append(ARGUMENT_PLACEHOLDER);

        arguments.add(attribute);
        return this;
    }

    public JDBCPreparedCommand dropTableIfExists(T element)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(element.name());

        return create();
    }

    public JDBCPreparedCommand dropTableIfExists(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCPreparedCommand dropTable(T element)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(element.name());

        return create();
    }

    public JDBCPreparedCommand dropTable(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCPreparedCommand create()
    {
        PreparedStatement preparedStatement;
        try
        {
            preparedStatement = jdbcConnection.get().prepareStatement(sqlCommandBuilder.toString());

            for (int i = 0; i < arguments.size(); ++i)
            {
                preparedStatement.setObject(i+1, arguments.get(i));
            }

        } catch (SQLException e)
        {
            throw new IllegalArgumentException("Invalid Query " + getCommand() + " " + e.getMessage(), e);
        }

        return new JDBCPreparedCommand(preparedStatement);
    }

}
