package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>>
{
    private static final String UPDATE = "update ";
    private static final String INSERT_INTO = "insert into ";
    private static final String REMOVE = "remove ";

    private String command = "";
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public enum SQLOperation
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
        command += UPDATE + element.name() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        command += INSERT_INTO + element.name() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        command += INSERT_INTO + clazz.getSimpleName() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> values(Object... args)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("values ( ");
        for(int i = 0;  i < args.length; ++i )
        {
            stringBuilder.append( " ? " );
            if ( i < args.length -1)
            {
                stringBuilder.append( " , " );
            }
            arguments.add(args[i]);
        }
        stringBuilder.append(")");

        command += stringBuilder.toString();

        return this;
    }

    public JDBCCommandBuilder<T> remove(T element)
    {
        command += REMOVE + element.name() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> from(T element)
    {
        command += "from " + element.name() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> from(Class<?> clazz)
    {
        command += "from " + clazz.getSimpleName() + " ";
        return this;
    }

    public JDBCCommandBuilder<T> where(T element)
    {
        command += "where " + element.name() + " ";
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
        return command;
    }

    public JDBCCommandBuilder<T> is(SQLOperation operation, Object attribute)
    {
        command += operation.toString() + " ? ";
        arguments.add(attribute);
        return this;
    }

    public JDBCPreparedCommand dropTableIfExists(T element)
    {
        command = "drop table if exists " + element.name();
        return create();
    }

    public JDBCPreparedCommand dropTableIfExists(Class<?> clazz)
    {
        command = "drop table if exists " + clazz.getSimpleName();
        return create();
    }

    public JDBCPreparedCommand dropTable(T element)
    {
        command = "drop table " + element.name();
        return create();
    }

    public JDBCPreparedCommand dropTable(Class<?> clazz)
    {
        command = "drop table " + clazz.getSimpleName();
        return create();
    }

    public JDBCPreparedCommand create()
    {
        PreparedStatement preparedStatement;
        try
        {
            preparedStatement = jdbcConnection.get().prepareStatement(command);

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
