package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.DROP_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.IF_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.INSERT_INTO;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.REMOVE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.UPDATE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>>
{
    private final StringBuilder sqlCommandBuilder = new StringBuilder();
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
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
        return is(EQUAL, value);
    }

    public JDBCCommandBuilder<T> isLessThan(Object value)
    {
        return is(LESS_THAN, value);
    }

    public JDBCCommandBuilder<T> isGreaterThan(Object value)
    {
        return is(GREATER_THAN, value);
    }


    public String getCommand()
    {
        return sqlCommandBuilder.toString();
    }

    public JDBCCommandBuilder<T> is(SQLSyntax.SQLOperation operation, Object attribute)
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
        return new JDBCPreparedCommand(jdbcConnection, sqlCommandBuilder.toString(), arguments );
    }

}
